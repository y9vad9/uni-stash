rootProject.name = "uni-stash-parallel-computing"

includeBuild(
    "build-conventions"
)

include(
    ":lab1",
    ":lab3",
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
