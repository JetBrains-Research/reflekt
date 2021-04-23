rootProject.name = "example"

include(":first-module")
include(":second-module")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        jcenter()
        google()
    }
}
