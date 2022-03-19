plugins {
    id("com.android.application")
    kotlin("android")
    id("me.2bab.scratchpaper")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "me.xx2bab.scratchpaper.sample"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "3.1.0"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}


dependencies {
    implementation(deps.kotlin.std)
    implementation("androidx.appcompat:appcompat:1.4.1")
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
    forceUpdateIcons = true // Can not be lazily set, it's valid only before "afterEvaluate{}".

    // ICON_OVERLAY styles, contents.
    style {
        textSize.set(9)
        textColor.set("#FFFFFFFF") // Accepts 3 kinds of format: "FFF", "FFFFFF", "FFFFFFFF".
        lineSpace.set(4)
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