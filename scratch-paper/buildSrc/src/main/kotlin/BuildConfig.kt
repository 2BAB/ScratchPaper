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
        val sdkCommon by lazy { "com.android.tools:sdk-common:30.0.3" }
        val sdkLib by lazy { "com.android.tools:sdklib:30.0.3" }
        const val polyfill = "me.2bab:polyfill:0.4.1"
        const val polyfillRes = "me.2bab:polyfill-res:0.4.1"

        const val jfreesvg = "org.jfree:jfreesvg:3.3"

        // Test
        const val junit = "junit:junit:4.12"
        const val mockito = "org.mockito:mockito-core:3.9.0"
        const val mockitoInline = "org.mockito:mockito-inline:3.9.0"
    }

}