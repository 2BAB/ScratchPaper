package me.xx2bab.scratchpaper.utils

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import java.io.File

class CacheUtils {

    companion object {

        fun mkdir(project: Project, variant: BaseVariant, dimension: String) {
            variant.preBuild.doLast {
                val cacheDir = getCacheDir(project, dimension)
                if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                    Logger.e("Can not create cache directory for ScratchPaper.")
                }
            }
        }

        fun getCacheDir(project: Project, dimension: String): File {
            return File(project.buildDir, "intermediates" + File.separator + "scratch-paper"
                    + File.separator + dimension.trim())
        }

    }

}