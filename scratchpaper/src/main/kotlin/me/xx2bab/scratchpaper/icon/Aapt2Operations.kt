package me.xx2bab.scratchpaper.icon

import me.xx2bab.polyfill.tools.CommandLineKit
import java.io.File

fun compileResDir(
    aapt2ExecutorPath: String, targetDir: File, resFiles: List<File>
) {
    CommandLineKit.runCommand("$aapt2ExecutorPath compile --legacy "
            + "-o ${targetDir.absolutePath} "
            + resFiles.joinToString(separator = " ") { it.absolutePath }
    )
}

