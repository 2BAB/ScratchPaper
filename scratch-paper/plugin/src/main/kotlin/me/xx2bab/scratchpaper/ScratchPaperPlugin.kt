package me.xx2bab.scratchpaper

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import me.xx2bab.scratchpaper.utils.CacheUtils
import me.xx2bab.scratchpaper.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScratchPaperPlugin : Plugin<Project> {

    private val extensionName = "scratchPaper"

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw IllegalStateException("'com.android.application' plugin required.")
        }
        Logger.init(project)

        val android = project.extensions.findByType(AppExtension::class.java)
        val variants = android!!.applicationVariants
        val config = project.extensions.create(extensionName, ScratchPaperExtension::class.java)

        variants.all { variant ->
            val dimension = variant.assembleProvider.get().name.replace("assemble", "")
            CacheUtils.mkdir(project, variant, dimension)

            val params = GeneratorParams(project, variant, dimension,
                    config, android)
            if (config.enableGenerateIconOverlay ?: variant.buildType.isDebuggable) {
                IconOverlayGenerator(params).process()
            }
            if (config.enableGenerateBuildInfo ?: variant.buildType.isDebuggable) {
                BuildInfoGenerator(params).process()
            }

        }

    }


}
