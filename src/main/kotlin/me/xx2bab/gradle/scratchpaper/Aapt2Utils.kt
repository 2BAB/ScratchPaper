package me.xx2bab.gradle.scratchpaper

import com.android.sdklib.BuildToolInfo
import org.gradle.api.Project
import java.io.File

class Aapt2Utils {

    companion object {

        fun compileResDir(project: Project, targetDir: File, resFiles: List<File>) {
            val androidPluginUtils = AndroidPluginUtils(project)
            val androidBuilder = androidPluginUtils.getAndroidBuilder()
            val aapt2ExecutorPath = androidBuilder?.buildToolInfo?.getPath(BuildToolInfo.PathId.AAPT2)

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

}