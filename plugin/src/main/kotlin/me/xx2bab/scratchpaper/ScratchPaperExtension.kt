package me.xx2bab.scratchpaper

import com.android.build.api.variant.Variant
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property
import java.awt.Color
import javax.inject.Inject

open class ScratchPaperExtension @Inject constructor(
    objects: ObjectFactory
) {

    var kotlinEnableByVariant: EnableByVariant? = null

    var groovyEnableByVariant: Closure<Boolean>? = null

    // For Gradle Kotlin DSL
    fun enableByVariant(selector: EnableByVariant) {
        kotlinEnableByVariant = selector
    }

    // For Gradle Groovy DSL
    fun enableByVariant(selector: Closure<Boolean>) {
        groovyEnableByVariant = selector.dehydrate()
    }

    var forceUpdateIcons: Boolean = false

    // Experimental field
    // @see IconOverlayGenerator#removeXmlIconFiles
    var enableXmlIconsRemoval = objects.property<Boolean>().convention(false)

    var iconNames = objects.property<String>().convention("ic_launcher, ic_launcher_round")

    val style: IconOverlayStyle = objects.newInstance(
        IconOverlayStyle::class.java
    )

    fun style(action: Action<IconOverlayStyle>) {
        action.execute(style)
    }

    val content: IconOverlayContent = objects.newInstance(
        IconOverlayContent::class.java
    )

    fun content(action: Action<IconOverlayContent>) {
        action.execute(content)
    }


    companion object {

        fun isFeatureEnabled(variant: Variant,
                             kotlinEnableByVariant: EnableByVariant?,
                             groovyEnableByVariant: Closure<Boolean>?
        ): Boolean = when {
            kotlinEnableByVariant != null -> {
                kotlinEnableByVariant.invoke(variant)
            }
            groovyEnableByVariant != null -> {
                groovyEnableByVariant.call(variant)
            }
            else -> false
        }


        fun parseBackgroundColor(backgroundColor: String): Color {
            val color: IntArray = hexColorToRGBIntArray(backgroundColor)
            return Color(color[1], color[2], color[3], color[0])
        }

        fun parseTextColor(textColor: String): Color {
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
}

internal typealias EnableByVariant = (variant: Variant) -> Boolean