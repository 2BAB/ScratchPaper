package me.xx2bab.scratchpaper.utils

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object CommandUtils {

    private var workingDir = File("./")

    fun runCommand(command: String, workingDir: File = CommandUtils.workingDir): String? {
        return try {
            val parts = command.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                    .directory(workingDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()

            proc.waitFor(1500, TimeUnit.MILLISECONDS)
            proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}