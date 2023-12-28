
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.gradle.testkit.runner.GradleRunner
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.util.concurrent.TimeUnit


class BasicFunctionalTest {

    companion object {

        private const val testProjectPath = "../sample"
        private const val spIntermediates =
            "./build/sample-%s/app/build/intermediates/scratch-paper/"

        @BeforeAll
        @JvmStatic
        fun buildTestProject() {
            if (File("../local.properties").exists()) {
                println("Publishing libraries to MavenLocal...")
                ("./gradlew" + " :scratchpaper:publishToMavenLocal"
                        + " --stacktrace").runCommand(File("../"))
                println("All libraries published.")
            }
            runBlocking(Dispatchers.IO) {
                agpVerProvider().map { agpVer ->
                    async {
                        println(
                            "Copying project for AGP [${agpVer}] from ${
                                File(testProjectPath).absolutePath
                            }..."
                        )

                        val targetProject = File("./build/sample-$agpVer")
                        targetProject.deleteRecursively()
                        File(testProjectPath).copyRecursively(targetProject)
                        val settings = File(targetProject, "settings.gradle.kts")
                        val newSettings = settings.readText()
                            .replace(
                                "= \"../\"",
                                "= \"../../../\""
                            ) // Redirect the base dir
                            .replace(
                                "enabledCompositionBuild = true",
                                "enabledCompositionBuild = false"
                            ) // Force the app to find plugin from maven local
                            .replace(
                                "getVersion(\"agpVer\")",
                                "\"$agpVer\""
                            ) // Hardcode agp version
                        settings.writeText(newSettings)

                        println("assembleFullDebug for [$agpVer]")

                        GradleRunner.create()
                            .withGradleVersion("8.5")
                            .forwardOutput()
                            .withArguments("clean", "assembleFullDebug", "--stacktrace")
                            .withProjectDir(targetProject)
                            .build()

                        println("Testing...")
                    }
                }.forEach {
                    it.await()
                }
            }
        }

        @JvmStatic
        fun agpVerProvider(): List<String> {
            val versions = File("../deps.versions.toml").readText()
            val regexPlaceHolder = "%s\\s\\=\\s\\\"([A-Za-z0-9\\.\\-]+)\\\""
            val getVersion = { s: String ->
                regexPlaceHolder.format(s).toRegex().find(versions)!!.groupValues[1]
            }
            return listOf(getVersion("agpVer"), getVersion("agpBackportVer"))
        }

        fun String.runCommand(workingDir: File) {
            ProcessBuilder(*split(" ").toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(15, TimeUnit.MINUTES)
        }
    }


    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun flavorSupport_Successfully(agpVer: String) {
        val fullDebug = File("${spIntermediates.format(agpVer)}/icons-fullDebug/")
        assertThat(fullDebug.exists(), `is`(true))
        assertThat(fullDebug.isDirectory, `is`(true))
        assertThat(fullDebug.listFiles().isNotEmpty(), `is`(true))
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun pngIconsAreGenerated_Successfully(agpVer: String) {
        listOf(
            "mipmap-xxxhdpi",
            "mipmap-xxhdpi",
            "mipmap-xhdpi",
            "mipmap-hdpi",
            "mipmap-mdpi"
        ).forEach {
            val normalAppIcon =
                File("${spIntermediates.format(agpVer)}/icons-fullDebug/$it/ic_launcher.png")
            val roundAppIcon =
                File("${spIntermediates.format(agpVer)}/icons-fullDebug/$it/ic_launcher_round.png")
            assertThat(normalAppIcon.exists(), `is`(true))
            assertThat(roundAppIcon.exists(), `is`(true))
        }
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun xmlIconsAreGenerated_Successfully(agpVer: String) {
        val normalAppIcon =
            File("${spIntermediates.format(agpVer)}/icons-fullDebug/mipmap-anydpi-v26/ic_launcher.xml")
        val roundAppIcon =
            File("${spIntermediates.format(agpVer)}/icons-fullDebug/mipmap-anydpi-v26/ic_launcher_round.xml")
        assertThat(normalAppIcon.exists(), `is`(true))
        assertThat(roundAppIcon.exists(), `is`(true))
        assertThat(normalAppIcon.readText(), StringContains.containsString("ic_launcher_overlay"))
        assertThat(
            roundAppIcon.readText(),
            StringContains.containsString("ic_launcher_round_overlay")
        )

        listOf(
            "ic_launcher_overlay.svg",
            "ic_launcher_overlay.xml",
            "ic_launcher_round_overlay.svg",
            "ic_launcher_round_overlay.xml"
        ).forEach {
            assertThat(
                File("${spIntermediates.format(agpVer)}/icons-fullDebug/drawable/$it").exists(),
                `is`(true)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("agpVerProvider")
    fun pngOverlayContentIsAttached_Correctly() {
        // TODO: Haven't found a simple&free OCR framework
        //  to fulfill the test requirement. Will add the test once seeked.
    }

}