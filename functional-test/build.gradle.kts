plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "me.2bab"

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(deps.kotlin.std)
    implementation(deps.kotlin.coroutine)
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
}

testing {
    suites {
        val functionalTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            testType.set(TestSuiteType.FUNCTIONAL_TEST)
            dependencies {
                implementation(deps.hamcrest)
                implementation("dev.gradleplugins:gradle-test-kit:7.4.2")
                implementation(deps.kotlin.coroutine)
                implementation(deps.kotlin.serialization)
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("functionalTest"))
}

tasks.withType<Test> {
    testLogging {
        this.showStandardStreams = true
    }
}