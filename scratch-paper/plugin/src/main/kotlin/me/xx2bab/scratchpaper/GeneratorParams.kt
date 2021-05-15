package me.xx2bab.scratchpaper

import com.android.build.api.variant.Variant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import me.xx2bab.polyfill.ApplicationPolyfill
import org.gradle.api.Project

data class GeneratorParams(val project: Project,
                           val classicVariant: ApplicationVariant,
                           val polyfill: ApplicationPolyfill,
                           val dimension: String,
                           val config: ScratchPaperExtension,
                           val android: AppExtension)