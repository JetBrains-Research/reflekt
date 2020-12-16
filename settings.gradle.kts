rootProject.name = "reflekt"

include(":reflekt-core")
include(":reflekt-dsl")
include(":reflekt-plugin")
include(":gradle-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}
