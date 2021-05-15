rootProject.name = "scratch-paper"

include(":plugin")

if (file("../../Polyfill").run { exists() && isDirectory }) {
    includeBuild("../../Polyfill") {
        dependencySubstitution {
            substitute(module("me.2bab:polyfill"))
                .with(project(":polyfill"))
        }
    }
}