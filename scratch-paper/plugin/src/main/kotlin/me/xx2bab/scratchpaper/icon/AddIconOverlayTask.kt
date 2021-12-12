package me.xx2bab.scratchpaper.icon

import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.matrix.tool.CommandLineKit
import me.xx2bab.scratchpaper.IconOverlayContent
import me.xx2bab.scratchpaper.IconOverlayStyle
import me.xx2bab.scratchpaper.ScratchPaperExtension
import me.xx2bab.scratchpaper.iconprocessor.compileResDir
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class AddIconOverlayTask : DefaultTask() {

    @get:Internal
    abstract val buildToolInfoProvider: Property<BuildToolInfo>

    @get:InputFiles
    abstract val allInputResourcesProvider: SetProperty<FileSystemLocation>

    @get:Input
    abstract val variantNameProvider: Property<String>

    @get:Input
    abstract val versionNameProvider: Property<String>

    @get:Input
    abstract val iconNamesProvider: Property<String>

    @get:Input
    abstract val enableXmlIconsRemovalProvider: Property<Boolean>

    @get:Nested
    abstract val styleConfigProvider: Property<IconOverlayStyle>

    @get:Nested
    abstract val contentConfigProvider: Property<IconOverlayContent>

    @get:OutputDirectory
    abstract val mergedResourceDirProvider: DirectoryProperty

    @get:OutputDirectory
    abstract val iconCacheDirProvider: DirectoryProperty

    @TaskAction
    fun addOverlay() {
        val processedIcons = arrayListOf<File>()
        val destDir = iconCacheDirProvider.get().asFile
        val mergedResourceDir = mergedResourceDirProvider.get().asFile
        val resDirs = allInputResourcesProvider.get().map { it.asFile }
        val iconNames = iconNamesProvider.get().split(",")
        val styleConfig = styleConfigProvider.get()
        val contentConfig = contentConfigProvider.get()

        val text = mutableListOf<String>()
        if (contentConfig.showVariantName.get()) { text.add(variantNameProvider.get()) }
        if (contentConfig.showVersionName.get()) { text.add(versionNameProvider.get()) }
        if (contentConfig.showGitShortId.get()) { text.add(generateGitShortId()) }
        if (contentConfig.showDateTime.get()) {text.add(generateDateTime()) }
        text.add(contentConfig.extraInfo.get() ?: "")

        val iconProcessorParam = IconProcessorParam(
            text = text,
            textSize = styleConfig.textSize.get(),
            textColor = ScratchPaperExtension.parseTextColor(styleConfig.textColor.get()),
            lineSpace = styleConfig.lineSpace.get(),
            bgColor = ScratchPaperExtension.parseBackgroundColor(styleConfig.backgroundColor.get())
        )


        // Add overlay to icons and generated to an intermediates dir
        findIcons(resDirs, iconNames).forEach { icon ->
            val icons = getProcessor(
                icon,
                destDir,
                iconProcessorParam
            )?.process()
            if (icons != null) {
                for (file in icons) {
                    processedIcons.add(file)
                }
            }
        }

        // Compiled images to .flat files using aapt2 and replaced previous compiled icons,
        // later .flat files will be fed to merged resource task.
        val aapt2ExecutorPath = buildToolInfoProvider.get().getPath(BuildToolInfo.PathId.AAPT2)
        compileResDir(aapt2ExecutorPath, mergedResourceDir, processedIcons)

        // [Optional] Remove original xml files
        if (enableXmlIconsRemovalProvider.isPresent && enableXmlIconsRemovalProvider.get()) {
            removeXmlIconFiles(iconNames, mergedResourceDir)
        }
    }

    private fun generateGitShortId(): String {
        var shortId = CommandLineKit.runCommand("git rev-parse --short HEAD")
        if (shortId == null || shortId.length > 8) {
            shortId = ""
        }
        return shortId
    }

    private fun generateDateTime(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM,HH:mm"))


    private fun findIcons(
        where: List<File>?,
        iconNames: List<String>
    ): Collection<File> {
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

    private fun isIconFile(namePrefix: String, file: File): Boolean {
        return file.isFile && file.nameWithoutExtension == namePrefix.trim()
    }


    private fun getProcessor(
        originIcon: File,
        destDir: File,
        iconProcessorParam: IconProcessorParam
    ): BaseIconProcessor? {
        if (!originIcon.exists()) {
            return null
        }
        return if (originIcon.extension == "xml") {
            AdaptiveIconProcessor(originIcon, destDir, iconProcessorParam)
        } else {
            RegularIconProcessor(originIcon, destDir, iconProcessorParam)
        }
    }

    /**
     * If the [AdaptiveIconProcessor] does not fulfill the requirement,
     * alternatively you can use this function to remove those adaptive icons
     * since ScratchPaper is working for QA testing products only.
     *
     * @param iconNames    the icons defined in the AndroidManifest.xml (icon & roundIcons)
     * @param mergedResDir it's a directory like /build/intermediates/res/merged/debug
     */
    private fun removeXmlIconFiles(iconNames: List<String>, mergedResDir: File) {
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


}
