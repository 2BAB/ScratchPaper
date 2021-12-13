package me.xx2bab.scratchpaper

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutput
import me.xx2bab.polyfill.ApplicationVariantPolyfill
import me.xx2bab.polyfill.agp.provider.BuildToolInfoProvider
import me.xx2bab.polyfill.agp.tool.toTaskContainer
import me.xx2bab.polyfill.res.ResourcesBeforeMergeAction
import me.xx2bab.polyfill.res.ResourcesMergeInputProvider
import me.xx2bab.scratchpaper.icon.AddIconOverlayTask
import me.xx2bab.scratchpaper.utils.CacheLocation
import me.xx2bab.scratchpaper.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class ScratchPaperPlugin : Plugin<Project> {

    private val extensionName = "scratchPaper"
    private val groupName = "scratch-paper"

    override fun apply(project: Project) {
        Logger.init(project)
        val config = project.extensions.create(extensionName, ScratchPaperExtension::class.java)

        val androidExt =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        androidExt.onVariants { variant ->
            val variantName = variant.name.capitalize()
            // TODO: find a better way to support both SINGLE and MULTI APKs version names
            val mainOutput: VariantOutput = variant.outputs[0]
            val polyfill = ApplicationVariantPolyfill(project, variant)

            // For icon overlay
            project.afterEvaluate {
                // Check feature flag
                if (!ScratchPaperExtension.isFeatureEnabled(
                        variant,
                        config.kotlinEnableByVariant,
                        config.groovyEnableByVariant
                    )
                ) {
                    return@afterEvaluate
                }

                // Add icon overlay
                val addIconOverlayTaskProvider = project.tasks.register<AddIconOverlayTask>(
                    "add${variantName}IconsOverlay"
                ) {
                    group = groupName
                    versionNameProvider.set(mainOutput.versionName)
                    variantNameProvider.set(variantName)
                    iconNamesProvider.set(config.iconNames)
                    enableXmlIconsRemovalProvider.set(config.enableXmlIconsRemoval)
                    mergedResourceDirProvider.set(variant.toTaskContainer().mergeResourcesTask.flatMap { it.outputDir })
                    iconCacheDirProvider.set(
                        CacheLocation.getCacheDir(
                            project,
                            "icons-${variant.name}"
                        )
                    )
                    allInputResourcesProvider.set(
                        polyfill.newProvider(ResourcesMergeInputProvider::class.java).obtain()
                    )
                    buildToolInfoProvider.set(
                        polyfill.newProvider(BuildToolInfoProvider::class.java).obtain()
                    )
                    styleConfigProvider.set(config.style)
                    contentConfigProvider.set(config.content)
                }
                polyfill.addAGPTaskAction(ResourcesBeforeMergeAction(addIconOverlayTaskProvider))

                // To decide whether the merge task should always run for
                // collecting the latest icons that con contain timestamp update.
                if (config.forceUpdateIcons) {
                    val mergeTask = variant.toTaskContainer().mergeResourcesTask.get()
                    mergeTask.outputs.upToDateWhen { false }
                }
            }

        }
    }

}
