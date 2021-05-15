package me.xx2bab.scratchpaper.utils

import com.android.build.api.variant.Variant
import com.android.build.gradle.api.ApplicationVariant
import com.android.sdklib.BuildToolInfo
import me.xx2bab.polyfill.ApplicationPolyfill
import me.xx2bab.polyfill.agp.provider.BuildToolProvider
import me.xx2bab.polyfill.matrix.tool.CommandLineKit
import org.gradle.api.Project
import java.io.File

object Aapt2Utils {

    fun compileResDir(
        variant: ApplicationVariant,
        polyfill: ApplicationPolyfill,
        targetDir: File,
        resFiles: List<File>
    ) {
        val aapt2ExecutorPath = polyfill
            .getProvider(variant, BuildToolProvider::class.java)
            .get()!!
            .getPath(BuildToolInfo.PathId.AAPT2)
        CommandLineKit.runCommand(
            "$aapt2ExecutorPath compile --legacy " +
                    "-o ${targetDir.absolutePath} " +
                    resFiles.map { it.absolutePath }.joinToString(separator = " ")
        )
    }

}