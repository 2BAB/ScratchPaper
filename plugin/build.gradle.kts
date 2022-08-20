plugins {
    id("java-gradle-plugin")
    `kotlin-dsl`
    `github-release`
    `maven-central-publish`
    `functional-test-setup`
    kotlin("plugin.serialization")
}

group = "me.2bab"
version = BuildConfig.Versions.scratchPaperVersion


java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    plugins {
        create("scratchpaper") {
            id = "me.2bab.scratchpaper"
            implementationClass = "me.xx2bab.scratchpaper.ScratchPaperPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())

    implementation(deps.kotlin.std)
    implementation(deps.kotlin.serialization)

    compileOnly(deps.android.gradle.plugin)
    compileOnly(deps.android.tools.sdkcommon)
    compileOnly(deps.android.tools.sdklib)

    implementation(deps.polyfill.main)
    implementation(deps.jfreesvg)

    testImplementation(gradleTestKit())
    testImplementation(deps.junit)
    testImplementation(deps.mockito)
    testImplementation(deps.mockitoInline)
}