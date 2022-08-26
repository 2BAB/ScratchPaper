package me.xx2bab.scratchpaper

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

open class IconOverlayStyle @Inject constructor(
    objects: ObjectFactory
) {

    @Input
    val textSize: Property<Int> = objects.property<Int>().convention(12)

    @Input
    val textColor: Property<String> = objects.property<String>().convention("#FFFFFFFF")

    @Input
    val lineSpace: Property<Int> = objects.property<Int>().convention(4)

    @Input
    val backgroundColor: Property<String> = objects.property<String>().convention("#CC000000")

}