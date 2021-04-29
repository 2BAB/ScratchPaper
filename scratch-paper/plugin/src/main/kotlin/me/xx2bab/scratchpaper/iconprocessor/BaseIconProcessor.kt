package me.xx2bab.scratchpaper.iconprocessor

import me.xx2bab.scratchpaper.ScratchPaperExtension
import org.gradle.api.Project
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.io.File

abstract class BaseIconProcessor(val project: Project,
                                 val dimension: String,
                                 val originIcon: File,
                                 val config: ScratchPaperExtension = ScratchPaperExtension.DEFAULT_CONFIG,
                                 private val lines: Array<out String>) {

    companion object {

        fun getProcessor(project: Project,
                         dimension: String,
                         image: File,
                         config: ScratchPaperExtension = ScratchPaperExtension.DEFAULT_CONFIG,
                         lines: Array<out String>): BaseIconProcessor? {
            if (!image.exists()) {
                return null
            }
            return if (image.extension == "xml") {
                AdaptiveIconProcessor(project, dimension, image, config, lines)
            } else {
                RegularIconProcessor(project, dimension, image, config, lines)
            }
        }

    }

    // to make sure the fontSize can be a regular number (like 11 12 14 that developers usually use)
    // I test 14 on all dpi generating, and found 96 (which is the xhdpi icon size) can fits it well
    // so we just make it as a standard size and compute the ratio for others to scale
    private val prettyImageSizeFits14FontSize = 96

    fun process(): Array<File> {
        val backgroundOverlayColor: Color = config.getBackgroundColor()
        val textColor: Color = config.getTextColor()

        val size = getSize()
        val imgWidth: Int = size.first
        val imgHeight: Int = size.second
        val ratio = imgWidth * 1.0 / prettyImageSizeFits14FontSize

        val fontSize: Int = (config.textSize * ratio).toInt()
        val linePadding: Int = (config.verticalLinePadding * ratio).toInt()
        val lineCount: Int = lines.size
        val totalLineHeight: Int = (fontSize * lineCount) + ((linePadding + 1) * lineCount)

        getGraphic().apply {
            this.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

            // Draw background overlay
            val marginTop = (imgHeight - totalLineHeight) / 2
            color = backgroundOverlayColor
            fillRect(0, marginTop, imgWidth, totalLineHeight)

            // Draw each line of text
            font = Font(Font.SANS_SERIF, Font.PLAIN, fontSize)
            color = textColor
            for ((i, line) in lines.reversed().withIndex()) {
                val strWidth = this.fontMetrics.stringWidth(line)

                var x = 0
                if (imgWidth >= strWidth) {
                    x = ((imgWidth - strWidth) / 2)
                }

                val y = imgHeight - (fontSize * i) - ((i + 1) * linePadding) - marginTop

                // drawString(line, x, y)
                drawText(line, x, y)
            }
        }
        return writeIcon()
    }

    abstract fun getSize(): Pair<Int, Int>

    abstract fun getGraphic(): Graphics2D

    abstract fun drawText(line: String, x: Int, y: Int)

    abstract fun writeIcon(): Array<File>


}