package me.xx2bab.scratchpaper

import com.android.build.gradle.tasks.MergeManifests
import com.android.build.gradle.tasks.MergeResources
import me.xx2bab.scratchpaper.iconprocessor.BaseIconProcessor
import me.xx2bab.scratchpaper.utils.Aapt2Utils
import me.xx2bab.scratchpaper.utils.Logger
import org.gradle.api.Project
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


class IconOverlayGenerator(private val params: GeneratorParams) {

    // default icon name of Android is ic_launcher
    private val defaultIconName = "ic_launcher"
    private val tagApplication = "application"
    private val attrIcon = "android:icon"
    private val attrRoundIcon = "android:roundIcon"

    fun process() {
        setAwtEnv()
        params.variant.outputs.forEach { output ->
            val processManifestTask = output.processManifest as MergeManifests
            val mergeResourcesTask = params.variant.mergeResources as MergeResources
            if (params.config.alwaysUpdateIconInfo) {
                output.processResources.outputs.upToDateWhen { false }
            }
            output.processResources.doFirst("process${params.dimension}IconsByScratchPaper") {
                val processedIcons = arrayListOf<File>()
                val version = "@" + params.variant.mergedFlavor.versionName
                val iconNames = getIconName(processManifestTask.manifestOutputDirectory)
                val resDirs = mergeResourcesTask.computeResourceSetList0()
                findIcons(resDirs, iconNames).forEach { icon ->
                    val icons = addTextToIcon(params.project, params.dimension,
                            icon, params.config, params.dimension, version, params.config.extraInfo)
                    if (icons != null) {
                        for (file in icons) {
                            processedIcons.add(file)
                        }
                    }
                }

                val mergeResTaskName = "merge${params.dimension}Resources"
                val mergeResTask = params.project.tasks.getByName(mergeResTaskName) as MergeResources
                val mergedResDir = mergeResTask.outputDir
                Aapt2Utils.compileResDir(params.project, mergedResDir, processedIcons)
                if (params.config.enableXmlIconRemove) {
                    removeXmlIconFiles(iconNames, mergedResDir)
                }
            }
        }
    }

    /**
     * To hack the awt on AS and Gradle building environment,
     * This is inherit from v1.x which forked from icon-version@akonior
     */
    private fun setAwtEnv() {
        // We want our font to come out looking pretty
        System.setProperty("awt.useSystemAAFontSettings", "on")
        System.setProperty("swing.aatext", "true")

        // Fix for Android Studio issue: Could not find class: apple.awt.CGraphicsEnvironment
        try {
            Class.forName(System.getProperty("java.awt.graphicsenv"))
        } catch (e: ClassNotFoundException) {
            Logger.e("java.awt.graphicsenv: $e")
            System.setProperty("java.awt.graphicsenv", "sun.awt.CGraphicsEnvironment")
        }

        //  Fix for AS issue: Toolkit not found: apple.awt.CToolkit
        try {
            Class.forName(System.getProperty("awt.toolkit"))
        } catch (e: ClassNotFoundException) {
            Logger.e("awt.toolkit: $e")
            System.setProperty("awt.toolkit", "sun.lwawt.macosx.LWCToolkit")
        }
    }

    /**
     * Icon name to search for in the app drawable folders
     * If no icon can be found in the manifest, IconOverlayGenerator#defaultIconName will be used
     */
    private fun getIconName(manifestDirectory: File): Array<String> {
        if (!manifestDirectory.exists()) {
            return arrayOf()
        }
        var mergedManifestFile = File(manifestDirectory, "AndroidManifest.xml") // default
        if (!mergedManifestFile.exists()) {
            // If the user enables split apk, we can traverse the folder
            // and find one AndroidManifest.xml, it's valid to return first one result,
            // because split feature does't change icon definition on AndroidManifest.xml
            val searchResult = manifestDirectory.walk().maxDepth(3)
                    .filter { file -> file.name == "AndroidManifest.xml" }
                    .firstOrNull()

            if (searchResult != null) {
                mergedManifestFile = searchResult
            }
        }
        if (!mergedManifestFile.exists()) {
            return arrayOf()
        }

        val manifestXml = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(mergedManifestFile)
        var regularIconName = manifestXml.getElementsByTagName(tagApplication).item(0)
                .attributes.getNamedItem(attrIcon)?.nodeValue
        var roundIconName = manifestXml.getElementsByTagName(tagApplication).item(0)
                .attributes.getNamedItem(attrRoundIcon)?.nodeValue
        regularIconName = regularIconName?.split("/")?.get(1) ?: defaultIconName
        roundIconName = roundIconName?.split("/")?.get(1) ?: defaultIconName + "_round"
        return arrayOf(regularIconName, roundIconName)
    }

    /**
     * Finds all icon files matching the icon specified in the given manifest.
     */
    private fun findIcons(where: List<File>?, iconNames: Array<String>): Collection<File> {
        val result: MutableSet<File> = hashSetOf()
        where?.forEach {
            it.walk().maxDepth(3)
                    .filter { dir ->
                        dir.name.contains("mipmap") || dir.name.contains("drawable")
                    }
                    .forEach { file ->
                        file.walk().forEach { image ->
                            iconNames.forEach { iconName ->
                                if (isIconFile(iconName, image)) {
                                    result.add(image)
                                }
                            }
                        }
                    }
        }
        return result
    }


    /**
     * Draws the given background and text over an image
     *
     * @param project   The Instance of org.gradle.api.Project
     * @param dimension The dimension contains buildType and flavor
     * @param image     The icon file that will be decorated
     * @param config    The configuration which controls how the overlay will appear
     * @param lines     The lines of text to be displayed
     */
    private fun addTextToIcon(project: Project,
                              dimension: String,
                              image: File,
                              config: ScratchPaperExtension = ScratchPaperExtension.DEFAULT_CONFIG,
                              vararg lines: String): Array<File>? {
        return BaseIconProcessor.getProcessor(project, dimension, image, config, lines)?.process()
    }

    /**
     * Experimental:
     * For now I didn't find an elegant approach to add a cover for xml icon,
     * so the ScratchPaper provide a temporary function to remove them.
     *
     * @param iconNames    the icons defined in the AndroidManifest.xml (icon & roundIcons)
     * @param mergedResDir it's a directory like /build/intermediates/res/merged/debug
     */
    private fun removeXmlIconFiles(iconNames: Array<String>, mergedResDir: File) {
        if (mergedResDir.isFile) {
            return
        }
        mergedResDir.walk().forEach { file ->
            iconNames.forEach { iconName ->
                if (file.isFile && file.name.contains("$iconName.xml.flat")) {
                    file.delete()
                }
            }

        }
    }

    private fun isIconFile(namePrefix: String, file: File): Boolean {
        return file.isFile && file.nameWithoutExtension == namePrefix
    }

    /**
     * To get all original resources including libraries
     *
     * @link github.com/Mobcase/McImage/blob/master/src/main/java/com/smallsoho/mcplugin/image/utils/HookUtil.kt
     * @author smallSohoSolo
     */
    private fun MergeResources.computeResourceSetList0(): List<File>? {
        val computeResourceSetListMethod = MergeResources::class.java.declaredMethods
                .firstOrNull { it.name == "computeResourceSetList" && it.parameterCount == 0 }
                ?: return null

        val oldIsAccessible = computeResourceSetListMethod.isAccessible
        try {
            computeResourceSetListMethod.isAccessible = true

            val resourceSets = computeResourceSetListMethod.invoke(this) as? Iterable<*>

            return resourceSets
                    ?.mapNotNull { resourceSet ->
                        val getSourceFiles = resourceSet?.javaClass?.methods?.find {
                            it.name == "getSourceFiles" && it.parameterCount == 0
                        }
                        val files = getSourceFiles?.invoke(resourceSet)
                        @Suppress("UNCHECKED_CAST")
                        files as? Iterable<File>
                    }
                    ?.flatten()

        } finally {
            computeResourceSetListMethod.isAccessible = oldIsAccessible
        }
    }

}