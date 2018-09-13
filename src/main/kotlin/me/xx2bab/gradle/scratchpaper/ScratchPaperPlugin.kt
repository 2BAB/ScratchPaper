package me.xx2bab.gradle.scratchpaper

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.tasks.MergeManifests
import com.android.build.gradle.tasks.MergeResources
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
        val variants = android!!.applicationVariants
        val config = project.extensions.create("scratchPaper", ScratchPaperExtension::class.java)

        variants.all { variant ->

            if (!variant.buildType.isDebuggable) {
                Logger.i("Skipping non-debuggable variant: ${variant.name}")
                return@all
            }

            val buildName = variant.flavorName + variant.buildType.name.capitalize()
            CacheUtils.mkdir(project, variant, buildName)
            ResUtils.setAwtEnv()

            variant.outputs.forEach { output ->
                val processManifestTask: MergeManifests = output.processManifest as MergeManifests

                output.processResources.doFirst {
                    val processedIcons = arrayListOf<File>()
                    val mergedManifestFile = File(processManifestTask.manifestOutputDirectory,
                            "AndroidManifest.xml")
                    val resDirs = variant.sourceSets[0].resDirectories
                    val version = "@" + variant.mergedFlavor.versionName
                    val iconName = ResUtils.getIconName(mergedManifestFile)
                    ResUtils.findIcons(resDirs, iconName).forEach { icon ->
                        val processedIcon = ResUtils.addTextToIcon(project, buildName,
                                icon, config, buildName, version, config.extraInfo)
                        processedIcons.add(processedIcon)
                    }

                    val mergeResTaskName = "merge${variant.buildType.name.capitalize()}Resources"
                    val mergeResTask = project.tasks.getByName(mergeResTaskName) as MergeResources
                    val mergedResDir = mergeResTask.outputDir
                    Aapt2Utils.compileResDir(project, mergedResDir, processedIcons)
                    if (config.enableXmlIconRemove) {
                        ResUtils.removeXmlIconFiles(iconName, mergedResDir)
                    }
                }
            }
        }

    }


}
