plugins {
    id("com.android.application")
    kotlin("android")
    id("me.2bab.scratchpaper")
}

android {
    namespace = "me.xx2bab.scratchpaper.sample"
    compileSdk = 34
    defaultConfig {
        applicationId = "me.xx2bab.scratchpaper.sample"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "3.3.0"
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

    flavorDimensions += "featureScope"
    productFlavors {
        create("demo") {
            dimension = "featureScope"
            applicationIdSuffix = ".demo"
            versionNameSuffix = "-demo"
        }
        create("full") {
            dimension = "featureScope"
            applicationIdSuffix = ".full"
            versionNameSuffix = "-full"
        }
    }

    splits {
        density {
            isEnable = true
            reset()
            include("mdpi")
            compatibleScreens("xlarge")
        }
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}


dependencies {
    implementation(deps.kotlin.std)
    implementation("androidx.appcompat:appcompat:1.6.1")
}

// Run `./gradlew clean assembleFullDebug` for testing
scratchPaper {
    // Main feature flags. !!! Mandatory field.
    // Can not be lazily set, it's valid only before "afterEvaluate{}".
    // In this way, only "FullDebug" variant will get icon overlays
    enableByVariant { variant ->
        variant.name.contains("debug", true)
                && variant.name.contains("full", true)
    }

    // !!! Mandatory field.
    // Can be lazily set even after configuration phrase.
    iconNames.set("ic_launcher, ic_launcher_round")

    // Some sub-feature flags
    enableXmlIconsRemoval.set(false) // Can be lazily set even after configuration phrase.
    forceUpdateIcons = false // Can not be lazily set, it's valid only before "afterEvaluate{}".

    // ICON_OVERLAY styles, contents.
    style {
        textSize.set(8)
        textColor.set("#FFFFFFFF") // Accepts 3 kinds of format: "FFF", "FFFFFF", "FFFFFFFF".
        lineSpace.set(2)
        backgroundColor.set("#99000000") // Same as textColor.
    }

    content {
        showVersionName.set(true)
        showVariantName.set(true)
        showGitShortId.set(true)
        showDateTime.set(true)
        extraInfo.set("For QA")
    }
}