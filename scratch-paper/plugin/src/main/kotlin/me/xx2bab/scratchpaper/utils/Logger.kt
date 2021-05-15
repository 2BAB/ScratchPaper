package me.xx2bab.scratchpaper.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

object Logger {

    private const val TAG = "[ScratchPaper]: "

    private lateinit var gradleLogger: Logger

    fun init (project: Project){
        gradleLogger = project.logger
    }

    fun d(message: String) {
        gradleLogger.debug(TAG + message)
    }

    fun i(message: String) {
        gradleLogger.info(TAG + message)
    }

    fun e(message: String) {
        gradleLogger.error(TAG + message)
    }

    fun l(message: String) {
        gradleLogger.lifecycle(TAG + message)
    }

}