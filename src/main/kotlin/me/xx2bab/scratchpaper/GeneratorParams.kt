package me.xx2bab.scratchpaper

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

data class GeneratorParams(val project: Project,
                           val variant: BaseVariant,
                           val dimension: String,
                           val config: ScratchPaperExtension,
                           val android: AppExtension)