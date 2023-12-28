package me.xx2bab.scratchpaper.icon

import com.android.sdklib.BuildToolInfo
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import me.xx2bab.polyfill.PolyfillAction
import me.xx2bab.polyfill.tools.CommandLineKit
import me.xx2bab.scratchpaper.IconOverlayContent
import me.xx2bab.scratchpaper.IconOverlayStyle
import me.xx2bab.scratchpaper.ScratchPaperExtension
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddIconOverlayTaskAction(
    private val buildToolInfoProvider: Provider<BuildToolInfo>,
    private val allInputResourcesProvider: Provider<List<Directory>>,
    private val variantName: String,
    private val versionName: Property<String?>,
    private val iconNamesProvider: Property<String>,
    private val enableXmlIconsRemovalProvider: Property<Boolean>,
    private val styleConfigProvider: IconOverlayStyle,
    private val contentConfigProvider: IconOverlayContent,
    private val iconCacheDirProvider: Provider<Directory>,
) : PolyfillAction<Directory> {

    override fun onTaskConfigure(task: Task) {
        task.inputs.property("variantName", variantName)
        task.inputs.property("versionName", versionName.get())
        task.inputs.property("iconNamesProvider", iconNamesProvider.get())
        task.inputs.property("enableXmlIconsRemovalProvider", enableXmlIconsRemovalProvider.get())

        styleConfigProvider.apply {
            val map = mapOf(
                "textSize" to textSize.get(),
                "textColor" to textColor.get(),
                "lineSpace" to lineSpace.get(),
                "backgroundColor" to backgroundColor.get()
            ).toJsonObject()
            task.inputs.property("styleConfigProvider", map.toString())
        }

        contentConfigProvider.apply {
            val map = mapOf(
                "showVersionName" to showVersionName.get(),
                "showVariantName" to showVariantName.get(),
                "showGitShortId" to showGitShortId.get(),
                "showDateTime" to showDateTime.get(),
                "extraInfo" to extraInfo.get()
            ).toJsonObject()
            task.inputs.property("contentConfigProvider", map.toString())
        }
    }


    override fun onExecute(mergedResourceDirProvider: Provider<Directory>) {
        val processedIcons = arrayListOf<File>()
        val destDir = iconCacheDirProvider.get().asFile
        val mergedResourceDir = mergedResourceDirProvider.get().asFile
        val resDirs = allInputResourcesProvider.get().map { it.asFile }
        val iconNames = iconNamesProvider.get().split(",")
        val styleConfig = styleConfigProvider
        val contentConfig = contentConfigProvider

        val text = mutableListOf<String>()
        if (contentConfig.showVariantName.get()) {
            text.add(variantName)
        }
        if (contentConfig.showVersionName.get()) {
            text.add(versionName.get())
        }
        if (contentConfig.showGitShortId.get()) {
            text.add(generateGitShortId())
        }
        if (contentConfig.showDateTime.get()) {
            text.add(generateDateTime())
        }
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
            icons?.forEach(processedIcons::add)
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

    private fun Map<*, *>.toJsonObject(): JsonObject = JsonObject(
        mapNotNull {
            (it.key as? String ?: return@mapNotNull null) to it.value.toJsonElement()
        }.toMap(),
    )

    private fun Any?.toJsonElement(): JsonElement = when (this) {
        null -> JsonNull
        is Map<*, *> -> toJsonElement()
        is Collection<*> -> toJsonElement()
        else -> JsonPrimitive(toString())
    }

}

