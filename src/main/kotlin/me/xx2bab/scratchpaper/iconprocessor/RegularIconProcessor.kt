package me.xx2bab.scratchpaper.iconprocessor

import me.xx2bab.scratchpaper.ScratchPaperExtension
import me.xx2bab.scratchpaper.utils.CacheUtils
import me.xx2bab.scratchpaper.utils.Logger
import org.gradle.api.Project
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class RegularIconProcessor(project: Project,
                           dimension: String,
                           originIcon: File,
                           config: ScratchPaperExtension,
                           lines: Array<out String>)
    : BaseIconProcessor(project, dimension, originIcon, config, lines) {

    override fun getSize(): Pair<Int, Int> {
        return Pair(bufferedImage.width, bufferedImage.height)
    }

    override fun getGraphic(): Graphics2D {
        return graphic
    }

    override fun drawText(line: String, x: Int, y: Int) {
        getGraphic().drawString(line, x, y)
    }

    override fun writeIcon(): Array<File> {
        val destDir = File(CacheUtils.getCacheDir(project, dimension), originIcon.parentFile.name)
        if (!destDir.exists() && !destDir.mkdirs()) {
            Logger.e("Can not create cache directory for ScratchPaper.")
        }
        val destIcon = File(destDir, originIcon.name)
        ImageIO.write(bufferedImage, "png", destIcon)
        return arrayOf(destIcon)
    }

    private val bufferedImage: BufferedImage = ImageIO.read(originIcon)
    private val graphic: Graphics2D

    init {
        graphic = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(bufferedImage)
    }

}