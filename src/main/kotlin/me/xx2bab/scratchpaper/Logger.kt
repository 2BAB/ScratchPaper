package me.xx2bab.scratchpaper

import org.gradle.api.Project
import org.gradle.api.logging.Logger

class Logger {

    companion object {

        private const val TAG = "[ScratchPaper]: "

        private var logUtil: Logger? = null

        fun init(project: Project) {
            logUtil = project.logger
        }

        fun d(message: String) {
            logUtil?.debug(TAG + message)
        }

        fun i(message: String) {
            logUtil?.info(TAG + message)
        }

        fun e(message: String) {
            logUtil?.error(TAG + message)
        }

    }

}