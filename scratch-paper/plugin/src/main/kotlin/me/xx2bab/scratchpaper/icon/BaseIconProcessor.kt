package me.xx2bab.scratchpaper.icon

import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.io.File

abstract class BaseIconProcessor(val originIcon: File,
                                 val destDir: File,
                                 val param: IconProcessorParam) {

    // to make sure the fontSize can be a regular number (like 11 12 14 that developers usually use)
    // I test 14 on all dpi generating, and found 96 (which is the xhdpi icon size) can fits it well
    // so we just make it as a standard size and compute the ratio for others to scale
    private val prettyImageSizeFits14FontSize = 96

    fun process(): Array<File> {
        val size = getSize()
        val imgWidth: Int = size.first
        val imgHeight: Int = size.second
        val ratio = imgWidth * 1.0 / prettyImageSizeFits14FontSize

        val fontSize: Int = (param.textSize * ratio).toInt()
        val linePadding: Int = (param.lineSpace * ratio).toInt()
        val lineCount: Int = param.text.size
        val totalLineHeight: Int = (fontSize * lineCount) + ((linePadding + 1) * lineCount)

        getGraphic().apply {
            this.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

            // Draw background overlay
            val marginTop = (imgHeight - totalLineHeight) / 2
            color = param.bgColor
            fillRect(0, marginTop, imgWidth, totalLineHeight)

            // Draw each line of text
            font = Font(Font.SANS_SERIF, Font.PLAIN, fontSize)
            color = param.textColor
            for ((i, line) in param.text.reversed().withIndex()) {
                if (line.isBlank()) {
                    continue
                }
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