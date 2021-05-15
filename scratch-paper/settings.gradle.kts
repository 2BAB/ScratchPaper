rootProject.name = "scratch-paper"

include(":plugin")

includeBuild("../../Polyfill"){
    dependencySubstitution {
        substitute(module("me.2bab:polyfill"))
            .with(project(":polyfill"))
    }
}