package me.xx2bab.gradle.scratchpaper

import com.android.tools.r8.com.google.common.collect.Lists
import java.awt.Color
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints.KEY_TEXT_ANTIALIASING
import java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilderFactory

class SPUtils {

    companion object {

        private const val DEFAULT_ICON_NAME: String = "ic_launcher"

        /**
         * Icon name to search for in the app drawable folders
         * if none can be found in the app manifest
         */
        private fun getIconName(manifestFile: File): String? {
            if (manifestFile.isDirectory || !manifestFile.exists()) {
                return ""
            }
            val manifestXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile)
            val fileName = manifestXml.getElementsByTagName("application").item(0)
                    .attributes.getNamedItem("android:icon")?.nodeValue
            return fileName?.split("/")?.get(1)
        }

        /**
         * Finds all icon files matching the icon specified in the given manifest.
         *
         * If no icon can be found in the manifest, a default of {@link SPUtils#DEFAULT_ICON_NAME} will be used
         */
        fun findIcons(where: Collection<File>, manifest: File): List<File> {
            val iconName: String = getIconName(manifest) ?: DEFAULT_ICON_NAME
            val result: MutableList<File> = Lists.newArrayList()
            where.forEach {
                it.walk()
                        .filter { dir ->
                            dir.name.contains("mipmap") || dir.name.contains("drawable")
                        }
                        .forEach { file ->
                            file.walk().forEach { image ->
                                if (image.isFile
                                        && image.nameWithoutExtension == iconName
                                        && image.extension != "xml") {
                                    result.add(image)
                                }
                            }
                        }
            }
            return result
        }


        /**
         * Draws the given text over an image
         *
         * @param image The image file which will be written too
         * @param config The configuration which controls how the overlay will appear
         * @param lines The lines of text to be displayed
         */
        fun addTextToImage(image: File,
                           config: ScratchPaperExtension = ScratchPaperExtension.DEFAULT_CONFIG,
                           vararg lines: String) {
            val bufferedImage: BufferedImage = ImageIO.read(image)
            val backgroundOverlayColor: Color = config.getBackgroundColor()
            val textColor: Color = config.getTextColor()
            val fontSize: Int = config.textSize
            val linePadding: Int = config.verticalLinePadding
            val imgWidth: Int = bufferedImage.width
            val imgHeight: Int = bufferedImage.width
            val lineCount: Int = lines.size
            val totalLineHeight: Int = (fontSize * lineCount) + ((linePadding + 1) * lineCount)

            GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(bufferedImage).apply {
                this.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)

                // Draw our background overlay
                this.color = backgroundOverlayColor
                this.fillRect(0, imgHeight - totalLineHeight, imgWidth, totalLineHeight)

                // Draw each line of our text
                this.font = Font(Font.SANS_SERIF, Font.PLAIN, fontSize)
                this.color = textColor
                for ((i, line) in lines.reversed().withIndex()) {
                    val strWidth = this.fontMetrics.stringWidth(line)

                    var x = 0
                    if (imgWidth >= strWidth) {
                        x = ((imgWidth - strWidth) / 2)
                    }

                    val y = imgHeight - (fontSize * i) - ((i + 1) * linePadding)

                    this.drawString(line, x, y)
                }
            }

            ImageIO.write(bufferedImage, "png", image)
        }
    }


}