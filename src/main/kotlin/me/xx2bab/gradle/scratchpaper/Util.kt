//package me.xx2bab.gradle.scratchpaper
//
//import com.google.common.collect.Lists
//import groovy.io.FileType
//import groovy.util.XmlSlurper
//
//import javax.imageio.ImageIO
//import java.awt.*
//import java.awt.image.BufferedImage
//import java.util.List
//
//import java.awt.RenderingHints.KEY_TEXT_ANTIALIASING
//import java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON
//import java.io.File
//
//
//    /**
//     * Icon name to search for in the app drawable folders
//     * if none can be found in the app manifest
//     */
//    const val DEFAULT_ICON_NAME : String = "ic_launcher"
//
//    fun getIconName(manifestFile : File) : String {
//        if (manifestFile.isDirectory || !manifestFile.exists()) {
//            return ""
//        }
//
//        val manifestXml = XmlSlurper().parse(manifestFile)
//        val fileName = manifestXml?.application?.@'android:icon'?.text()
//        return fileName ? fileName?.split("/")[1] : null
//    }
//
//
//
//    /**
//     * Finds all icon files matching the icon specified in the given manifest.
//     *
//     * If no icon can be found in the manifest, a default of {@link Util#DEFAULT_ICON_NAME} will be used
//     */
//    static List<File> findIcons(File where, File manifest) {
//        String iconName = getIconName(manifest) ?: DEFAULT_ICON_NAME
//        List<File> result = Lists.newArrayList();
//
//        where.eachDirMatch(~/^mipmap.*|^drawable.*/) { dir ->
//            dir.eachFileMatch(FileType.FILES, ~"^${iconName}.*") { file ->
//                result.add(file)
//            }
//        }
//
//        return result
//    }
//
//    /**
//     * Draws the given text over an image
//     *
//     * @param image The image file which will be written too
//     * @param config The configuration which controls how the overlay will appear
//     * @param lines The lines of text to be displayed
//     */
//    fun addTextToImage(image : File,
//                       me.xx2bab.gradle.scratchpaper.IconCoverConfig config = me.xx2bab.gradle.scratchpaper.IconCoverConfig.DEFAULT_CONFIG,
//                       String... lines) {
//        final BufferedImage bufferedImage = ImageIO.read(image);
//
//        final Color backgroundOverlayColor = config.getBackgroundColor();
//        final Color textColor = config.getTextColor();
//        final int fontSize = config.textSize;
//        final int linePadding = config.verticalLinePadding;
//        final int imgWidth = bufferedImage.width;
//        final int imgHeight = bufferedImage.width;
//        final int lineCount = lines.length;
//        final int totalLineHeight = (fontSize * lineCount) + ((linePadding + 1) * lineCount);
//
//        GraphicsEnvironment.localGraphicsEnvironment.createGraphics(bufferedImage).with { g ->
//            g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
//
//            // Draw our background overlay
//            g.setColor(backgroundOverlayColor);
//            g.fillRect(0, imgHeight - totalLineHeight, imgWidth, totalLineHeight);
//
//            // Draw each line of our text
//            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
//            g.setColor(textColor)
//            lines.reverse().eachWithIndex { String line, int i ->
//                final int strWidth = g.getFontMetrics().stringWidth(line);
//
//                int x = 0;
//                if (imgWidth >= strWidth) {
//                    x = ((imgWidth - strWidth) / 2);
//                }
//
//                int y = imgHeight - (fontSize * i) - ((i + 1) * linePadding);
//
//                g.drawString(line, x, y);
//            }
//        }
//
//        ImageIO.write(bufferedImage, "png", image);
//    }
