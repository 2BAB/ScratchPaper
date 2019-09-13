package me.xx2bab.scratchpaper.utils

import com.android.sdklib.BuildToolInfo
import org.gradle.api.Project
import java.io.File

object Aapt2Utils {

    fun compileResDir(project: Project,
                      androidPluginUtils: AndroidPluginUtils,
                      targetDir: File,
                      resFiles: List<File>) {
        val aapt2ExecutorPath = androidPluginUtils.buildToolInfo().getPath(BuildToolInfo.PathId.AAPT2)

        project.exec { execSpec ->
            execSpec.executable(aapt2ExecutorPath)
            execSpec.args("compile")
            execSpec.args("--legacy")
            execSpec.args("-o")
            execSpec.args(targetDir.absolutePath)
            resFiles.forEach {
                execSpec.args(it.absolutePath)
            }
        }
    }

}