package me.xx2bab.gradle.scratchpaper

import org.gradle.api.Project
import java.awt.Color

open class IconCoverConfig(project: Project) {

    // val DEFAULT_CONFIG = IconCoverConfig()

    val textSize = 12

    val textColor = "#FFFFFFFF"

    val verticalLinePadding = 4

    val backgroundColor = "#CC000000"

    val extraInfo = ""


    fun getBackgroundColor(): Color {
        val color: IntArray = hexColorToRGBIntArray(backgroundColor)
        return Color(color[1], color[2], color[3], color[0])
    }

    fun getTextColor(): Color {
        val color: IntArray = hexColorToRGBIntArray(textColor)
        return Color(color[1], color[2], color[3], color[0])
    }


    fun hexColorToRGBIntArray(hexColor: String): IntArray {
        val processedHexColor: String
        if (!hexColor.startsWith("#")) {
            throw IllegalArgumentException()
        } else {
            processedHexColor = hexColor.replace("#", "")
        }
        val argbIntArray: IntArray = intArrayOf(0, 0, 0, 0)
        val colorLength = processedHexColor.length
        val octetHexColor: String

        octetHexColor = when (colorLength) {
            3 -> "FF" + processedHexColor.substring(0, 1) + processedHexColor.substring(0, 1) + processedHexColor.substring(1, 2) + processedHexColor.substring(1, 2) + processedHexColor.substring(2, 3) + processedHexColor.substring(2, 3)

            6 -> "FF$processedHexColor"

            8 -> processedHexColor

            else -> {
                throw IllegalArgumentException()
            }
        }

        for (i in 0..4) {
            argbIntArray[i] = Integer.parseInt(octetHexColor.substring(2 * i, 2 * i + 1)
                    + octetHexColor.substring(2 * i + 1, 2 * i + 2), 16)
        }

        return argbIntArray
    }
}