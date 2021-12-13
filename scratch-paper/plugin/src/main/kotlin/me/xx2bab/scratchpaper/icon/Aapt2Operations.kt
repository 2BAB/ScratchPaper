package me.xx2bab.scratchpaper.iconprocessor

import me.xx2bab.polyfill.matrix.tool.CommandLineKit
import java.io.File
import java.util.*

fun compileResDir(
    aapt2ExecutorPath: String, targetDir: File, resFiles: List<File>
) {
    CommandLineKit.runCommand("$aapt2ExecutorPath compile --legacy "
            + "-o ${targetDir.absolutePath} "
            + resFiles.joinToString(separator = " ") { it.absolutePath }
    )
}

