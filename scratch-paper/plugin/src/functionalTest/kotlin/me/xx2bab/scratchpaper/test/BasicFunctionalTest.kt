package me.xx2bab.scratchpaper.test

import com.alibaba.fastjson.JSON
import me.xx2bab.scratchpaper.BuildInfo
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class BasicFunctionalTest {

    companion object {

        private const val testProjectPath = "../../"
        private const val spIntermediates = "${testProjectPath}/test-app/build/intermediates/scratch-paper/"

        @BeforeClass
        @JvmStatic
        fun buildTestProject() {
            println("Building...")
            GradleRunner.create()
                .forwardOutput()
                .withArguments("clean", "assembleDebug", "--stacktrace")
                .withProjectDir(File(testProjectPath))
                .build()

            println("Testing...")
        }
    }

    @Test
    fun flavorSupport_Successfully() {
        val demoDebug = File("$spIntermediates/DemoDebug/")
        val fullDebug = File("$spIntermediates/FullDebug/")
        Assert.assertTrue(demoDebug.exists())
        Assert.assertTrue(demoDebug.isDirectory)
        Assert.assertTrue(demoDebug.listFiles().isNotEmpty())
        Assert.assertTrue(fullDebug.exists())
        Assert.assertTrue(fullDebug.isDirectory)
        Assert.assertTrue(fullDebug.listFiles().isNotEmpty())
    }

    @Test
    fun pngIconsAreGenerated_Successfully() {
        listOf(
            "mipmap-xxxhdpi",
            "mipmap-xxhdpi",
            "mipmap-xhdpi",
            "mipmap-hdpi",
            "mipmap-mdpi"
        ).forEach {
            val normalAppIcon = File("$spIntermediates/DemoDebug/$it/ic_launcher.png")
            val roundAppIcon = File("$spIntermediates/DemoDebug/$it/ic_launcher_round.png")
            Assert.assertTrue(normalAppIcon.exists())
            Assert.assertTrue(roundAppIcon.exists())
        }
    }

    @Test
    fun xmlIconsAreGenerated_Successfully() {
        val normalAppIcon = File("$spIntermediates/DemoDebug/mipmap-anydpi-v26/ic_launcher.xml")
        val roundAppIcon = File("$spIntermediates/DemoDebug/mipmap-anydpi-v26/ic_launcher_round.xml")
        Assert.assertTrue(normalAppIcon.exists())
        Assert.assertTrue(roundAppIcon.exists())
        Assert.assertTrue(normalAppIcon.readText().contains("ic_launcher_overlay"))
        Assert.assertTrue(roundAppIcon.readText().contains("ic_launcher_round_overlay"))

        listOf(
            "ic_launcher_overlay.svg",
            "ic_launcher_overlay.xml",
            "ic_launcher_round_overlay.svg",
            "ic_launcher_round_overlay.xml"
        ).forEach {
            Assert.assertTrue(File("$spIntermediates/DemoDebug/drawable/$it").exists())
        }
    }

    @Test
    fun metaJsonIsGenerated_Successfully() {
        val metaJson = "$spIntermediates/DemoDebug/assets/scratch-paper.json"
        val jsonFile = File(metaJson)
        Assert.assertTrue(jsonFile.exists())
        val buildInfo = JSON.parseObject(jsonFile.readText(),
            BuildInfo::class.javaObjectType)
        Assert.assertTrue(buildInfo.base.versionName.isNotBlank())
        Assert.assertTrue(buildInfo.base.buildTime.isNotBlank())
        Assert.assertTrue(buildInfo.base.buildType.isNotBlank())
        Assert.assertTrue(buildInfo.git.branch.isNotBlank())
        Assert.assertTrue(buildInfo.git.head.isNotBlank())
        Assert.assertTrue(buildInfo.dependencies.isNotEmpty())
        Assert.assertTrue(buildInfo.dependencies[0].list.isNotEmpty())
    }

    @Test
    fun pngOverlayContentIsAttached_Correctly() {
        // TODO: Haven't found a simple&royal-free OCR framework
        //  to fulfill the test requirement. Will add the test once seeked.
    }

}