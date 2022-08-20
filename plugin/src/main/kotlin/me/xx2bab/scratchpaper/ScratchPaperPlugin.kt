package me.xx2bab.scratchpaper

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutput
import me.xx2bab.polyfill.*
import me.xx2bab.scratchpaper.icon.AddIconOverlayTaskAction
import me.xx2bab.scratchpaper.utils.CacheLocation
import me.xx2bab.scratchpaper.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create

class ScratchPaperPlugin : Plugin<Project> {

    private val extensionName = "scratchPaper"
    private val groupName = "scratch-paper"

    override fun apply(project: Project) {
        Logger.init(project)
        project.apply(plugin = "me.2bab.polyfill")
        val config = project.extensions.create<ScratchPaperExtension>(extensionName)

        val androidExt =
            project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        androidExt.onVariants { variant ->
            val variantName = variant.getCapitalizedName()
            // TODO: find a better way to support both SINGLE and MULTI APKs version names
            val mainOutput: VariantOutput = variant.outputs[0]

            // For icon overlay
            // Check feature flag
            if (!ScratchPaperExtension.isFeatureEnabled(
                    variant,
                    config.kotlinEnableByVariant,
                    config.groovyEnableByVariant
                )
            ) {
                return@onVariants
            }

            // Add icon overlay
            val addIconOverlayTaskProvider = AddIconOverlayTaskAction(
                buildToolInfoProvider = variant.getBuildToolInfo(project),
                allInputResourcesProvider = variant.artifactsPolyfill.getAll(
                    PolyfilledMultipleArtifact.ALL_RESOURCES
                ),
                variantName = variantName,
                versionName = mainOutput.versionName,
                iconNamesProvider = config.iconNames,
                enableXmlIconsRemovalProvider = config.enableXmlIconsRemoval,
                styleConfigProvider = config.style,
                contentConfigProvider = config.content,
                iconCacheDirProvider = CacheLocation.getCacheDir(project, "icons-${variant.name}")
            )
            variant.artifactsPolyfill.use(
                action = addIconOverlayTaskProvider,
                toInPlaceUpdate = PolyfilledSingleArtifact.MERGED_RESOURCES
            )

            // To decide whether the merge task should always run for
            // collecting the latest icons that con contain timestamp update.
            if (config.forceUpdateIcons) {
                project.afterEvaluate {
                    val mergeTask = variant.getTaskContainer().mergeResourcesTask.get()
                    mergeTask.outputs.upToDateWhen { false }
                }
            }

        }
    }

}
