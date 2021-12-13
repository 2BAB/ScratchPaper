package me.xx2bab.scratchpaper.icon

import me.xx2bab.scratchpaper.utils.Logger
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class RegularIconProcessor(
    originIcon: File,
    destDir: File,
    param: IconProcessorParam
) : BaseIconProcessor(originIcon, destDir, param) {

    private val bufferedImage: BufferedImage = ImageIO.read(originIcon)
    private val graphic: Graphics2D = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(bufferedImage)

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
        // prepare destination file and its directory.
        // Creating a directory is to separate images from different pixel dimensions,
        // and support the AAPT2 compiling correctly(it requires the resource file
        // is put inside a res-dimension dir like "mipmap-xxhdpi").
        val imageParentFile = File(destDir, originIcon.parentFile.name)
        if (!imageParentFile.exists() && !imageParentFile.mkdirs()) {
            Logger.e("Can not create cache directory for ScratchPaper.")
        }
        val destIcon = File(imageParentFile, originIcon.name)
        ImageIO.write(bufferedImage, "png", destIcon)
        return arrayOf(destIcon)
    }

}