import java.util.Properties
apply(plugin = "com.jfrog.bintray")

val projectName = "scratch-paper"
val mavenDesc = "Add text on your Android app icon"
val baseUrl = "https://github.com/2BAB/ScratchPaper"
val siteUrl = baseUrl
val gitUrl = "${baseUrl}.git"
val issueUrl = "${baseUrl}/issues"

val licenseIds = "Apache-2.0"
val licenseNames = arrayOf("The Apache Software License, Version 2.0")
val licenseUrls = arrayOf("http://www.apache.org/licenses/LICENSE-2.0.txt")
val inception = "2018"

val username = "2BAB"


configure<PublishingExtension> {
    publications {
        create<MavenPublication>("scratchPaperPlugin") {
            from(components["java"])
            pom {
                // Description
                name.set(projectName)
                description.set(mavenDesc)
                url.set(siteUrl)

                // Archive
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                // License
                inceptionYear.set(inception)
                licenses {
                    licenseNames.forEachIndexed { ln, li ->
                        license {
                            name.set(li)
                            url.set(licenseUrls[ln])
                        }
                    }
                }
                developers {
                    developer {
                        name.set(username)
                    }
                }
                scm {
                    connection.set(gitUrl)
                    developerConnection.set(gitUrl)
                    url.set(siteUrl)
                }
            }
        }
    }


}


var btUser: String?
        var btApiKey: String?

if (project.extra["travisBuild"] as Boolean) {
    btUser = System.getenv("BINTRAY_USER")
    btApiKey = System.getenv("BINTRAY_API_KEY")
} else {
    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    btUser = properties.getProperty("bintray.user")
    btApiKey = properties.getProperty("bintray.apikey")
}

configure<com.jfrog.bintray.gradle.BintrayExtension> {
    user = btUser
    key = btApiKey
    setPublications("scratchPaperPlugin")
    pkg.apply {
        repo = "maven"
        name = projectName
        desc = mavenDesc
        websiteUrl = siteUrl
        issueTrackerUrl = issueUrl
        vcsUrl = gitUrl
        setLabels("2BAB", "Gradle", "ScratchPaper", "Overlay", "BuildType", "Debug")
        setLicenses(licenseIds)
        publish = true
        publicDownloadNumbers = true
    }
}