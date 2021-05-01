import BuildConfig.Deps

plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    `github-release`
    `maven-central-publish`
    `functional-test-setup`
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    plugins {
        create("scratchpaper") {
            id = "me.2bab.scratchpaper"
            implementationClass ="me.xx2bab.scratchpaper.ScratchPaperPlugin"
        }
    }
}

dependencies {
    implementation(kotlin(Deps.ktStd))
    implementation(kotlin(Deps.ktReflect))
    implementation(gradleApi())
    implementation(Deps.fastJson)
    compileOnly(Deps.agp)
    // implementation(Deps.polyfill)
    implementation(Deps.jfreesvg)

    testImplementation(gradleTestKit())
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
    testImplementation(Deps.mockitoInline)
}