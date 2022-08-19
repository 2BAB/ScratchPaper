package me.xx2bab.scratchpaper

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class IconOverlayContent @Inject constructor(
    objects: ObjectFactory
) {

    @Input
    val showVersionName: Property<Boolean> = objects.property<Boolean>().convention(true)

    @Input
    val showVariantName: Property<Boolean> = objects.property<Boolean>().convention(true)

    @Input
    val showGitShortId: Property<Boolean> = objects.property<Boolean>().convention(true)

    @Input
    val showDateTime: Property<Boolean> = objects.property<Boolean>().convention(true) // dd-MM,HH:mm

    @Input
    val extraInfo: Property<String> = objects.property<String>().convention("")
}