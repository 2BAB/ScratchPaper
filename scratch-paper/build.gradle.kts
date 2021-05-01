buildscript {
    val props = java.util.Properties()
    file("./buildSrc/src/main/resources/versions.properties").inputStream().use { props.load(it) }

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = props["kotlinVersion"]?.toString()))
    }
}

allprojects {
    group = "me.2bab"
    version = BuildConfig.Versions.scratchPaperVersion

    repositories {
        google()
        mavenCentral()
    }
}



//project.extra["travisBuild"] = System.getenv("TRAVIS") == "true"
//
//if (project.extensions.findByName("buildScan") != null) {
//    gradleEnterprise {
//        buildScan {
//            termsOfServiceUrl = "https://gradle.com/terms-of-service"
//            termsOfServiceAgree = "yes"
//        }
//    }
//}