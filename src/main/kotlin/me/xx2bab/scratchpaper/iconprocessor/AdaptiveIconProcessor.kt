package me.xx2bab.scratchpaper.iconprocessor

import com.android.ide.common.vectordrawable.Svg2Vector
import me.xx2bab.scratchpaper.ScratchPaperExtension
import me.xx2bab.scratchpaper.utils.CacheUtils
import me.xx2bab.scratchpaper.utils.Logger
import org.gradle.api.Project
import org.jfree.graphics2d.svg.SVGGraphics2D
import org.jfree.graphics2d.svg.SVGUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.Graphics2D
import java.awt.font.TextLayout
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


/**
 * In this Processor, we generate the SVG for overlay at first. Then we convert it
 * to Android Vector Drawable XML file, and merge it with the original icon. To deal
 * with SVG stuffs, we use "org.jfree:jfreesvg:3.3" now. If you need more advanced
 * functions, can choose "org.apache.xmlgraphics:batik-svgpp:1.10" as well.
 *
 * @link https://github.com/jfree/jfreesvg
 * @link https://github.com/jfree/jfree-demos
 * @link https://xmlgraphics.apache.org/batik/using/svg-generator.html
 */
class AdaptiveIconProcessor(project: Project,
                            dimension: String,
                            originIcon: File,
                            config: ScratchPaperExtension,
                            lines: Array<out String>)
    : BaseIconProcessor(project, dimension, originIcon, config, lines) {

    private val attrDrawable = "android:drawable"
    private val tagForeground = "foreground"
    private val tagLayerList = "layer-list"
    private val tagItem = "item"
    private val tagAdaptiveIcon = "adaptive-icon"

    override fun getSize(): Pair<Int, Int> {
        return Pair(width, height)
    }

    override fun getGraphic(): Graphics2D {
        return graphic
    }

    override fun drawText(line: String, x: Int, y: Int) {
        // Do not use Graphics2D.drawString(line: String, x: Int, y: Int), it will generates String
        // with <Text> in SVG file, and Android Vector Converter doesn't support <Text>.
        // Since we want to get a good compatible experience, so we draw text in <Path> to solve it.
        // @see com.android.ide.common.vectordrawable.Svg2Vector#unsupportedSvgNodes
        val tl = TextLayout(line, getGraphic().font, getGraphic().fontRenderContext)
        tl.draw(getGraphic(), x.toFloat(), y.toFloat())
    }

    override fun writeIcon(): Array<File> {
        // prepare destination file
        val destDir = File(CacheUtils.getCacheDir(project, dimension), originIcon.parentFile.name)
        if (!destDir.exists() && !destDir.mkdirs()) {
            Logger.e("Can not create cache directory for ScratchPaper.")
        }
        val destIcon = File(destDir, originIcon.name).apply { createNewFile() }

        // generate overlay svg & convert svg to vector drawable xml
        val commonDrawableDir = File(destIcon.parentFile.parent + File.separator + "drawable").apply { mkdir() }
        val overlaySVG = File(commonDrawableDir, "${destIcon.nameWithoutExtension}_overlay.svg")
        val overlayVectorDrawableFileName = "${destIcon.nameWithoutExtension}_overlay.xml"
        val overlayVectorDrawable = File(commonDrawableDir, overlayVectorDrawableFileName).apply { createNewFile() }
        SVGUtils.writeToSVG(overlaySVG, (getGraphic() as SVGGraphics2D).svgElement)
        val out = overlayVectorDrawable.outputStream()
        Svg2Vector.parseSvgToXml(overlaySVG, out)

        // append overlay to <foreground>
        val itemDrawableElement = originIconXmlDoc.createElement(tagItem)
        val drawableAttr = originIconXmlDoc.createAttribute(attrDrawable)
        drawableAttr.value = "@drawable/$overlayVectorDrawableFileName".removeSuffix(".xml")
        itemDrawableElement.setAttributeNode(drawableAttr)
        layerList.appendChild(itemDrawableElement)

        // write to destination
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(originIconXmlDoc)
        val result = StreamResult(destIcon)
        transformer.transform(source, result)


        return arrayOf(destIcon, overlayVectorDrawable)
    }

    private val width = 100
    private val height = 100
    private val graphic: Graphics2D
    private val originIconXmlDoc: Document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().parse(originIcon)
    private var layerList: Element

    init {

        // parse foreground node & clean all attributes and childs
        var drawableInForeground: String? = null

        var foregroundElement = originIconXmlDoc.getElementsByTagName(tagForeground).item(0)
        if (foregroundElement != null) {
            if (foregroundElement.attributes.getNamedItem(attrDrawable) != null) {
                drawableInForeground = foregroundElement.attributes.getNamedItem(attrDrawable).nodeValue
                foregroundElement.attributes.removeNamedItem(attrDrawable)
            }
        } else {
            foregroundElement = originIconXmlDoc.createElement(tagForeground)
            val adaptiveIconNode = originIconXmlDoc.getElementsByTagName(tagAdaptiveIcon).item(0)
            adaptiveIconNode.appendChild(foregroundElement)
        }

        // add a <layer-list> as its only one child
        // append all childs & attributes to new <foreground>
        layerList = originIconXmlDoc.createElement(tagLayerList)
        foregroundElement.appendChild(layerList)

        if (drawableInForeground != null) {
            val itemDrawableElement = originIconXmlDoc.createElement(tagItem)
            val drawableAttr = originIconXmlDoc.createAttribute(attrDrawable)
            drawableAttr.value = drawableInForeground
            itemDrawableElement.setAttributeNode(drawableAttr)
            layerList.appendChild(itemDrawableElement)
        }

        // init a new Graphic2D object to draw overlay
        graphic = SVGGraphics2D(width, height)
    }


}