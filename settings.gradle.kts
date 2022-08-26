rootProject.name = "scratch-paper-root"

pluginManagement {
    val versions = file("deps.versions.toml").readText()
    val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
    val getVersion = { s: String -> regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1] }

    plugins {
        kotlin("jvm") version getVersion("kotlinVer") apply false
        kotlin("plugin.serialization") version getVersion("kotlinVer") apply false
    }
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("deps") {
            from(files("deps.versions.toml"))
        }
    }
}

include(":scratchpaper", ":functional-test")

//if (file("../../Polyfill").run { exists() && isDirectory }) {
//    includeBuild("../../Polyfill") {
//        dependencySubstitution {
//            substitute(module("me.2bab:polyfill"))
//                .with(project(":polyfill"))
//        }
//    }
//}