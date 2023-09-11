rootProject.name = "example"

include(":first-module")
include(":second-module")

pluginManagement {
    repositories {
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        // add the dependency to Reflekt Maven repository
        // Uncomment to use a released version
        // maven("https://packages.jetbrains.team/maven/p/reflekt/reflekt")
        gradlePluginPortal()
    }
}
