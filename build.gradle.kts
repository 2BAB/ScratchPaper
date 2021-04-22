// must keep the order among `buildscript` `plugins` `repositories` blocks
buildscript {
    group = "me.2bab"
    version = "2.5.3.1-SNAPSHOT"

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.3.72"
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
    implementation("com.android.tools.build:gradle:4.1.2")
    implementation("org.jfree:jfreesvg:3.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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