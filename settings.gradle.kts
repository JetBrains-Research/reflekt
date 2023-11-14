rootProject.name = "reflekt"

include(":reflekt-plugin")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("using-embedded-kotlin")
