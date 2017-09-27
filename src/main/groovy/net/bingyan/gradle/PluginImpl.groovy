package net.bingyan.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.build.gradle.tasks.ProcessAndroidResources
import org.gradle.api.Plugin
import org.gradle.api.Project

import static net.bingyan.gradle.Util.addTextToImage
import static net.bingyan.gradle.Util.findIcons

class PluginImpl implements Plugin<Project> {

    void apply(Project project) {

        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new IllegalStateException("'com.android.application' plugin required.")
        }

        IconCoverConfig config = project.extensions.create("iconCoverConfig", IconCoverConfig, project)

        def log = project.logger
        project.android.applicationVariants.all { BaseVariant variant ->

            if (!variant.buildType.debuggable) {
                log.info "IconVersionPlugin. Skipping non-debuggable variant: $variant.name"
                return
            }

            log.info "IconVersionPlugin. Processing variant: $variant.name"
            variant.outputs.each { BaseVariantOutput output ->
                output.processResources.doFirst {
                    ProcessAndroidResources task = delegate
                    variant.outputs.each { BaseVariantOutput variantOutput ->
                        File manifest = output.processManifest.manifestOutputFile

                        File resDir = task.resDir
                        log.info "Looking for icon files in: $resDir.absolutePath"

                        findIcons(resDir, manifest).each { File icon ->
                            log.info "Adding flavor name and version to: " + icon.absolutePath

                            def buildName = variant.flavorName + " " + variant.buildType.name
                            def version = variant.versionName

                            addTextToImage(icon, config, buildName, version, config.extraInfo)
                        }
                    }
                }
            }
        }
    }

}
