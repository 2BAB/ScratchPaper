package me.xx2bab.gradle.scratchpaper

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.MergeManifests
import com.android.build.gradle.tasks.MergeResources
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ScratchPaperPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw IllegalStateException("'com.android.application' plugin required.")
        }
        Logger.init(project)

        val android = project.extensions.findByType(AppExtension::class.java)
        val variants: DomainObjectCollection<out BaseVariant> = android!!.applicationVariants
        val config = project.extensions.create("iconCoverConfig", ScratchPaperExtension::class.java)

        variants.all { variant ->

            if (!variant.buildType.isDebuggable) {
                Logger.i("IconVersionPlugin. Skipping non-debuggable variant: ${variant.name}")
                return@all
            }
            val buildName = variant.flavorName + " " + variant.buildType.name
            CacheUtils.mkdir(project, variant, buildName)

            variant.outputs.forEach { output ->
                val processManifestTask: MergeManifests = output.processManifest as MergeManifests

                output.processResources.doFirst {
                    val mergedManifestFile = File(processManifestTask.manifestOutputDirectory,
                            "AndroidManifest.xml")
                    val resDirs = variant.sourceSets[0].resDirectories
                    val version = "@" + variant.mergedFlavor.versionName
                    val processedIcons = arrayListOf<File>()
                    ResUtils.setAwtEnv()
                    ResUtils.findIcons(resDirs, mergedManifestFile).forEach { icon ->
                        val processedIcon = ResUtils.addTextToImage(project, buildName,
                                icon, config, buildName, version, config.extraInfo)
                        processedIcons.add(processedIcon)
                    }

                    val mergeResourceTaskName = "merge${variant.buildType.name.capitalize()}Resources"
                    val mergeResourceTask = project.tasks.getByName(mergeResourceTaskName) as MergeResources
                    val mergedResDir = mergeResourceTask.outputDir
                    Aapt2Utils.compileResDir(project, mergedResDir, processedIcons)
                }
            }
        }

    }


}
