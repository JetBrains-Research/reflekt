rootProject.name = "reflekt"

include(":reflekt-core")
include(":reflekt-dsl")
include(":reflekt-plugin")
include(":gradle-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        // Uncomment it for using the last kotlin compiler version
        // The full list of the build can be found here:
        // https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_BuildNumber?mode=builds&tag=bootstrap
        // (see builds with <boostrap> tag)
        // Note: uncomment it also in the build.gradle.kts
//        maven {
//            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
//        }
    }
}
