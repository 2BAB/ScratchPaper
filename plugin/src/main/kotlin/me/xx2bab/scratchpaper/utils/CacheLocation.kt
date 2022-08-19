package me.xx2bab.scratchpaper.utils

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import java.io.File

object CacheLocation {

    fun getCacheDir(project: Project, dimension: String): Provider<Directory> =
        project.layout.buildDirectory.dir(
            "intermediates" + File.separator + "scratch-paper"
                    + File.separator + dimension.trim()
        )

}