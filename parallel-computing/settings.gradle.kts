enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
    ":lab6",
    ":lab7",
    ":lab8",
)

include(":mathpar")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.dcm4che.org/")
    }
}
