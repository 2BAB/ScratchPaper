package me.xx2bab.scratchpaper

import java.awt.Color

open class ScratchPaperExtension {

    companion object {
        val DEFAULT_CONFIG = ScratchPaperExtension()
    }

    var enableGenerateIconOverlay: Boolean? = null

    var enableGenerateBuildInfo: Boolean? = null

    var textSize = 12

    var textColor = "#FFFFFFFF"

    var verticalLinePadding = 4

    var backgroundColor = "#CC000000"

    var extraInfo = ""

    // Experimental field
    // @see IconOverlayGenerator#removeXmlIconFiles
    var enableXmlIconRemove = false


    fun getBackgroundColor(): Color {
        val color: IntArray = hexColorToRGBIntArray(backgroundColor)
        return Color(color[1], color[2], color[3], color[0])
    }

    fun getTextColor(): Color {
        val color: IntArray = hexColorToRGBIntArray(textColor)
        return Color(color[1], color[2], color[3], color[0])
    }


    private fun hexColorToRGBIntArray(hexColor: String): IntArray {
        val processedHexColor: String
        if (!hexColor.startsWith("#")) {
            throw IllegalArgumentException()
        } else {
            processedHexColor = hexColor.replace("#", "")
        }
        val argbIntArray = intArrayOf(0, 0, 0, 0)
        val colorLength = processedHexColor.length
        val octetHexColor: String

        octetHexColor = when (colorLength) {
            3 -> "FF" + processedHexColor.let {
                val builder = StringBuilder()
                for (i in 0..2) {
                    builder.append(it.substring(i, i + 1)).append(it.substring(i, i + 1))
                }
                builder.toString()
            }

            6 -> "FF$processedHexColor"

            8 -> processedHexColor

            else -> {
                throw IllegalArgumentException()
            }
        }

        for (i in 0..3) {
            argbIntArray[i] = octetHexColor.substring(2 * i, 2 * i + 2).toInt(16)
        }

        return argbIntArray
    }
}