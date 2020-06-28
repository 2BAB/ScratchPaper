// must keep the order among `buildscript` `plugins` `repositories` blocks
import java.util.Properties
buildscript {

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    `bintray-plugin`

}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}


configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jre8")
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.android.tools.build:gradle:4.0.0")
    implementation("org.jfree:jfreesvg:3.3")
}

tasks.compileJava {
    options.compilerArgs.plusAssign(arrayOf("-proc:none"))
}


project.extra["travisBuild"] = System.getenv("TRAVIS") == "true"

if (project.extensions.findByName("buildScan") != null) {
    gradleEnterprise {
        buildScan {
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

// publish
group = "me.2bab"
version = "2.5.2"
