package me.xx2bab.scratchpaper

import com.android.tools.r8.com.google.common.collect.Lists
import org.gradle.api.Project
import java.awt.Color
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints.KEY_TEXT_ANTIALIASING
import java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilderFactory

class ResUtils {

    companion object {

        // default icon name of Android is ic_launcher
        private const val DEFAULT_ICON_NAME: String = "ic_launcher"

        // to make sure the fontSize can be a regular number (like 14, 16, 18 that develops usually use)
        // I test 14 on all dpi generating, and found 96 (which is the xhdpi icon size) can fits it well
        // so we just make it as a standard size and compute the ratio for others to scale
        private const val PRETTY_IMAGE_SIZE_FITS_14_FONT_SIZE = 96.0

        /**
         * To hack the awt on AS and Gradle building environment,
         * This is inherit from v1.x which forked from icon-version@akonior
         */
        fun setAwtEnv() {
            // We want our font to come out looking pretty
            System.setProperty("awt.useSystemAAFontSettings", "on")
            System.setProperty("swing.aatext", "true")

            // Fix for Android Studio issue: Could not find class: apple.awt.CGraphicsEnvironment
            try {
                Class.forName(System.getProperty("java.awt.graphicsenv"))
            } catch (e: ClassNotFoundException) {
                Logger.e("java.awt.graphicsenv: $e")
                System.setProperty("java.awt.graphicsenv", "sun.awt.CGraphicsEnvironment")
            }

            //  Fix for AS issue: Toolkit not found: apple.awt.CToolkit
            try {
                Class.forName(System.getProperty("awt.toolkit"))
            } catch (e: ClassNotFoundException) {
                Logger.e("awt.toolkit: $e")
                System.setProperty("awt.toolkit", "sun.lwawt.macosx.LWCToolkit")
            }
        }

        /**
         * Icon name to search for in the app drawable folders
         * If no icon can be found in the manifest, ResUtils#DEFAULT_ICON_NAME will be used
         */
        fun getIconName(manifestFile: File): String {
            if (manifestFile.isDirectory || !manifestFile.exists()) {
                return ""
            }
            val manifestXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile)
            val fileName = manifestXml.getElementsByTagName("application").item(0)
                    .attributes.getNamedItem("android:icon")?.nodeValue
            return fileName?.split("/")?.get(1) ?: DEFAULT_ICON_NAME
        }

        /**
         * Finds all icon files matching the icon specified in the given manifest.
         */
        fun findIcons(where: Collection<File>, iconName: String): List<File> {
            val result: MutableList<File> = Lists.newArrayList()
            where.forEach {
                it.walk()
                        .filter { dir ->
                            dir.name.contains("mipmap") || dir.name.contains("drawable")
                        }
                        .forEach { file ->
                            file.walk().forEach { image ->
                                if (isIconFile(iconName, image)
                                        && image.extension != "xml") {
                                    result.add(image)
                                }
                            }
                        }
            }
            return result
        }


        /**
         * Draws the given background and text over an image
         *
         * @param project   The Instance of org.gradle.api.Project
         * @param buildName The Instance of org.gradle.api.Project
         * @param image     The icon file that will be decorated
         * @param config    The configuration which controls how the overlay will appear
         * @param lines     The lines of text to be displayed
         */
        fun addTextToIcon(project: Project,
                          buildName: String,
                          image: File,
                          config: ScratchPaperExtension = ScratchPaperExtension.DEFAULT_CONFIG,
                          vararg lines: String): File {
            val bufferedImage: BufferedImage = ImageIO.read(image)
            val backgroundOverlayColor: Color = config.getBackgroundColor()
            val textColor: Color = config.getTextColor()

            val imgWidth: Int = bufferedImage.width
            val imgHeight: Int = bufferedImage.height
            val ratio = imgWidth / PRETTY_IMAGE_SIZE_FITS_14_FONT_SIZE

            val fontSize: Int = (config.textSize * ratio).toInt()
            val linePadding: Int = (config.verticalLinePadding * ratio).toInt()
            val lineCount: Int = lines.size
            val totalLineHeight: Int = (fontSize * lineCount) + ((linePadding + 1) * lineCount)

            GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(bufferedImage).apply {
                this.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)

                // Draw background overlay
                this.color = backgroundOverlayColor
                this.fillRect(0, imgHeight - totalLineHeight, imgWidth, totalLineHeight)

                // Draw each line of text
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
            val destDir = File(CacheUtils.getCacheDir(project, buildName), image.parentFile.name)
            if (!destDir.exists() && !destDir.mkdirs()) {
                Logger.e("Can not create cache directory for ScratchPaper.")
            }
            val destImage = File(destDir, image.name)
            ImageIO.write(bufferedImage, "png", destImage)
            return destImage
        }

        /**
         * Experimental:
         * For now I didn't find an elegant approach to add a cover for xml icon,
         * so the ScratchPaper provide a temporary function to remove them.
         *
         * @param iconName     the icon defined in the AndroidManifest.xml
         * @param mergedResDir it's a directory like /build/intermediates/res/merged/debug
         */
        fun removeXmlIconFiles(iconName: String, mergedResDir: File) {
            if (mergedResDir.isFile) {
                return
            }
            mergedResDir.walk().forEach { file ->
                if (file.isFile
                        && (file.name.contains("$iconName.xml.flat")
                                || file.name.contains("${iconName}_round.xml.flat"))) {
                    file.delete()
                }
            }
        }

        private fun isIconFile(namePrefix: String, file: File): Boolean {
            return file.isFile
                    && (file.nameWithoutExtension == namePrefix
                    || file.nameWithoutExtension == "${namePrefix}_round")
        }

    }


}