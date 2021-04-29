import java.util.*

object BuildConfig {

    val props = Properties()

    init {
        javaClass.classLoader.getResourceAsStream("versions.properties")
            .use { props.load(it) }
    }

    object Versions {
        val scratchPaperVersion by lazy { props["scratchPaperVersion"].toString() }
    }

    object Deps {
        const val ktStd = "stdlib-jdk8"
        const val ktReflect = "reflect"
        val agp by lazy { "com.android.tools.build:gradle:${props["agpVersion"]}" }
        const val polyfill = "me.2bab:polyfill:0.2.1"

        const val jfreesvg = "org.jfree:jfreesvg:3.3"

        // Test
        const val junit = "junit:junit:4.12"
        const val mockito = "org.mockito:mockito-core:3.9.0"
        const val mockitoInline = "org.mockito:mockito-inline:3.9.0"
        const val easyOCR = "cn.easyproject:easyocr:3.0.4-RELEASE"
    }

}