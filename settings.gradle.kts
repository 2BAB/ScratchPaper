rootProject.name = "scratch-paper-root"
include(":test-app")
includeBuild("scratch-paper"){
    dependencySubstitution {
        substitute(module("me.2bab:scratchpaper"))
            .with(project(":plugin"))
    }
}