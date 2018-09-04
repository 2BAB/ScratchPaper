package me.xx2bab.gradle.scratchpaper

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.MergeManifests
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class PluginImpl : Plugin<Project> {

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin(AppPlugin::class.java)) {
            throw IllegalStateException("'com.android.application' plugin required.")
        }

        setAwtEnv()

        val variants: DomainObjectCollection<out BaseVariant> = when {
            project.plugins.hasPlugin("com.android.application") -> {
                val ext = project.extensions.findByType(AppExtension::class.java)
                ext!!.applicationVariants
            }

            project.plugins.hasPlugin("com.android.test") -> {
                val ext = project.extensions.findByType(TestExtension::class.java)
                ext!!.applicationVariants
            }

            project.plugins.hasPlugin("com.android.library") -> {
                val ext = project.extensions.findByType(LibraryExtension::class.java)
                ext!!.libraryVariants
            }

            else -> throw IllegalArgumentException(
                    "Dexcount plugin requires the Android plugin to be configured")
        }

        val config = project.extensions.create("iconCoverConfig",
                IconCoverConfig::class.java, project)

        val log = project.logger
        variants.all { variant ->

            if (!variant.buildType.isDebuggable) {
                log.info("IconVersionPlugin. Skipping non-debuggable variant: ${variant.name}")
                return@all
            }
            val checkManifestTask = variant.checkManifest
            checkManifestTask.doLast {
                log.info(checkManifestTask.toString())
            }
            log.info("IconVersionPlugin. Processing variant: ${variant.name}")
            variant.outputs.forEach { output ->
                val processManifestTask : MergeManifests = output.processManifest as MergeManifests
                processManifestTask.doLast {
                    val mergedManifestFile = File(processManifestTask.manifestOutputDirectory, "AndroidManifest.xml")
                    log.info("${mergedManifestFile.exists()}  ${mergedManifestFile.absolutePath}")
                }


                output.processResources.doFirst {
                    //                    val task: ProcessAndroidResources = it
//                    variant.outputs.forEach { variantOutput ->
//                        val manifest = output.processManifest.manifestOutputDirectory
//
//                        val resDir = task.res
//                        log.info("Looking for icon files in: $resDir.absolutePath")
//
//                        Util.findIcons(resDir, manifest).each {
//                            File icon ->
//                            log.info "Adding flavor name and version to: "+icon.absolutePath
//
//                            val buildName = variant.flavorName + " " + variant.buildType.name
//                            val version = ""
//
//                            Util.addTextToImage(icon, config, buildName, version, config.extraInfo)
//                        }
//                    }
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
