package me.xx2bab.gradle.scratchpaper

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import java.io.File

class CacheUtils {

    companion object {

        fun mkdir(project: Project, variant: BaseVariant, buildName: String) {
            variant.preBuild.doLast {
                val cacheDir = getCacheDir(project, buildName)
                if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                    Logger.e("Can not create cache directory for ScratchPaper.")
                }
            }
        }

        fun getCacheDir(project: Project, buildName: String): File {
            return File(project.buildDir, "intermediates" + File.separator + "scratch-paper"
                    + File.separator + buildName.trim())
        }

    }

}