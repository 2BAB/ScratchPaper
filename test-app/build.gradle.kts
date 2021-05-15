import java.util.Properties
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    kotlin("android")
    id("me.2bab.scratchpaper")
}
val props = Properties()
file("../scratch-paper/buildSrc/src/main/resources/versions.properties").inputStream().use { props.load(it) }

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")
    defaultConfig {
        applicationId = "me.xx2bab.scratchpaper.sample"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "2.6.0"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions("version")
    productFlavors {
        create("demo") {
            setDimension("version")
            applicationIdSuffix = ".demo"
            versionNameSuffix = "-demo"
        }
        create("full") {
            setDimension("version")
            applicationIdSuffix = ".full"
            versionNameSuffix = "-full"
        }
    }

    splits {
        density {
            isEnable = true
            reset()
            include ("mdpi")
            compatibleScreens("xlarge")
        }
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${props["kotlinVersion"]}")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    implementation("com.github.pvarry:android-json-viewer:v1.1")
}

scratchPaper {
    textSize = 9
    textColor = "#FFFFFFFF"
    verticalLinePadding = 4
    backgroundColor = "#99000000"
    extraInfo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd,HH:mm"))
//    extraInfo = "This is a sample."
    enableGenerateIconOverlay = true
    enableGenerateBuildInfo = true
    enableXmlIconRemove = false
    enableVersionNameSuffixDisplay = true
}