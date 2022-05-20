rootProject.name = "example"

include(":first-module")
include(":second-module")

pluginManagement {
    repositories {
        mavenLocal()
        // add the dependency to Reflekt Maven repository
        // Uncomment to use a released version
        // maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
        gradlePluginPortal()
    }
}
