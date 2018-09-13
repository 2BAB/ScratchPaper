package me.xx2bab.gradle.scratchpaper

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BasePlugin
import com.android.builder.core.AndroidBuilder
import org.gradle.api.Project


class AndroidPluginUtils(val project: Project) {

    @Throws(Exception::class)
    fun getAndroidBuilder(): AndroidBuilder? {
        val basePlugin = project.plugins.findPlugin(AppPlugin::class.java)
        return if (null == basePlugin) {
            null
        } else {
            getField(BasePlugin::class.java, basePlugin, "androidBuilder") as AndroidBuilder
        }
    }

    private fun <T> getField(clazz: Class<T>, instance: T, fieldName: String): Any {
        val field = clazz.declaredFields.filter { it.name == fieldName }[0]
        field.isAccessible = true
        return field.get(instance) as Any
    }

}