rootProject.name = "scratch-paper-root"

pluginManagement {
    extra["externalDependencyBaseDir"] = "../"
    val versions =
        file(extra["externalDependencyBaseDir"].toString() + "deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion =
        { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        kotlin("android") version getVersion("kotlinVer") apply false
        id("com.android.application") version getVersion("agpVer") apply false
        kotlin("plugin.serialization") version getVersion("kotlinVer") apply false
//        id("me.2bab.scratchpaper") version "3.2.0" apply false
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "me.2bab.scratchpaper") {
                // It will be replaced by a local module using `includeBuild` below,
                // thus we just put a generic version (+) here.
                useModule("me.2bab:scratchpaper:+")
            }
        }
    }
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

val externalDependencyBaseDir = extra["externalDependencyBaseDir"].toString()
val enabledCompositionBuild = true

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("deps") {
            from(files(externalDependencyBaseDir + "deps.versions.toml"))
        }
    }
}

include(":app")
if (enabledCompositionBuild) {
    includeBuild(externalDependencyBaseDir) {
        dependencySubstitution {
            substitute(module("me.2bab:scratchpaper"))
                .with(project(":scratchpaper"))
        }
    }
}