package me.xx2bab.scratchpaper.utils

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.internal.scope.GlobalScope
import com.android.builder.core.AndroidBuilder
import com.android.builder.model.Version
import org.gradle.api.Project


class AndroidPluginUtils(val project: Project) {

    @Throws(Exception::class)
    fun getAndroidBuilder(): AndroidBuilder {
        val basePlugin = project.plugins.findPlugin(AppPlugin::class.java) as BasePlugin<*>
        val currentAGPVersion = AGPVersion(Version.ANDROID_GRADLE_PLUGIN_VERSION)
        val androidBuilderApiMinAGPVersion = AGPVersion("3.2.1")
        return if (currentAGPVersion < androidBuilderApiMinAGPVersion) {
            getField(BasePlugin::class.java, basePlugin,
                    "androidBuilder") as AndroidBuilder
        } else {
            val scope = getField(BasePlugin::class.java, basePlugin,
                    "globalScope") as GlobalScope
            scope.androidBuilder
        }
    }

    private fun <T> getField(clazz: Class<T>, instance: T, fieldName: String): Any {
        val field = clazz.declaredFields.filter { it.name == fieldName }[0]
        field.isAccessible = true
        return field.get(instance) as Any
    }


}