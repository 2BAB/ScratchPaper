package me.xx2bab.scratchpaper

import com.android.build.gradle.AppExtension
import me.xx2bab.polyfill.PolyfillFactory
import me.xx2bab.scratchpaper.utils.CacheUtils
import me.xx2bab.scratchpaper.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScratchPaperPlugin : Plugin<Project> {

    private val extensionName = "scratchPaper"

    override fun apply(project: Project) {
        Logger.init(project)

        val polyfill = PolyfillFactory.createApplicationPolyfill(project)
        val config = project.extensions.create(extensionName, ScratchPaperExtension::class.java)
        val classicAppExt = project.extensions.findByType(AppExtension::class.java)!!
        polyfill.onClassicVariants { classicVariant ->
            val dimension = classicVariant.assembleProvider.get().name.replace("assemble", "")
            CacheUtils.mkdir(project, classicVariant, dimension)
            val params = GeneratorParams(
                project,
                classicVariant,
                polyfill,
                dimension,
                config,
                classicAppExt
            )
            if (config.enableGenerateIconOverlay ?: classicVariant.buildType.isDebuggable) {
                IconOverlayGenerator(params).process()
            }
            if (config.enableGenerateBuildInfo ?: classicVariant.buildType.isDebuggable) {
                BuildInfoGenerator(params).process()
            }
        }
    }

}
