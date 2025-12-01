rootProject.name = "uni-stash-parallel-computing"

includeBuild(
    "build-conventions"
)

include(
    ":lab1",
    ":lab2",
    ":lab3",
    ":lab4",
    ":lab5",
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
