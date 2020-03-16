package me.xx2bab.scratchpaper.utils

import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.BasePlugin
import com.android.build.gradle.internal.scope.GlobalScope
import com.android.sdklib.BuildToolInfo
import org.gradle.api.Project


class AndroidPluginUtils(val project: Project) {

    @Throws(Exception::class)
    fun buildToolInfo(): BuildToolInfo {
        val basePlugin = project.plugins.findPlugin(AppPlugin::class.java) as BasePlugin
        val scope = getField(BasePlugin::class.java, basePlugin,
                "globalScope") as GlobalScope
        return scope.sdkComponents.buildToolInfoProvider.get()
    }

    fun <T> getField(clazz: Class<T>, instance: T, fieldName: String): Any {
        val field = clazz.declaredFields.filter { it.name == fieldName }[0]
        field.isAccessible = true
        return field.get(instance) as Any
    }

}