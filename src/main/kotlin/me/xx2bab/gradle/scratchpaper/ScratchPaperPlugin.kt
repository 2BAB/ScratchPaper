package me.xx2bab.gradle.scratchpaper

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.MergeManifests
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ScratchPaperPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw IllegalStateException("'com.android.application' plugin required.")
        }
        val log = project.logger
        setAwtEnv()

        val android = project.extensions.findByType(AppExtension::class.java)
        val variants: DomainObjectCollection<out BaseVariant> = android!!.applicationVariants

        val config = project.extensions.create("iconCoverConfig", ScratchPaperExtension::class.java)


        variants.all { variant ->

            if (!variant.buildType.isDebuggable) {
                log.info("IconVersionPlugin. Skipping non-debuggable variant: ${variant.name}")
                return@all
            }

            variant.outputs.forEach { output ->
                val processManifestTask: MergeManifests = output.processManifest as MergeManifests

                output.processResources.doFirst {
                    val mergedManifestFile = File(processManifestTask.manifestOutputDirectory,
                            "AndroidManifest.xml")
                    val resDirs = variant.sourceSets[0].resDirectories
                    val buildName = variant.flavorName + " " + variant.buildType.name
                    val version = ""
                    SPUtils.findIcons(resDirs, mergedManifestFile).forEach{ icon ->
                        SPUtils.addTextToImage(icon, config, buildName, version, config.extraInfo)
                    }
                }
            }
        }
    }

    private fun setAwtEnv() {
        // We want our font to come out looking pretty
        System.setProperty("awt.useSystemAAFontSettings", "on")
        System.setProperty("swing.aatext", "true")

        // Fix for Android Studio issue: Could not find class: apple.awt.CGraphicsEnvironment
        try {
            Class.forName(System.getProperty("java.awt.graphicsenv"))
        } catch (e: ClassNotFoundException) {
            System.err.println("[WARN] java.awt.graphicsenv: $e")
            System.setProperty("java.awt.graphicsenv", "sun.awt.CGraphicsEnvironment")
        }

        //  Fix for AS issue: Toolkit not found: apple.awt.CToolkit
        try {
            Class.forName(System.getProperty("awt.toolkit"))
        } catch (e: ClassNotFoundException) {
            System.err.println("[WARN] awt.toolkit: $e")
            System.setProperty("awt.toolkit", "sun.lwawt.macosx.LWCToolkit")
        }
    }

}
