rootProject.name = "example"

include(":first-module")
include(":second-module")

pluginManagement {
    // Uncomment to use a released version
//    resolutionStrategy {
//        this.eachPlugin {
//
//            if (requested.id.id == "io.reflekt") {
//                useModule("io.reflekt:gradle-plugin:${this.requested.version}")
//            }
//        }
//    }

    repositories {
        gradlePluginPortal()
        mavenLocal()
        // add the dependency to Reflekt Maven repository
        // Uncomment to use a released version
        // maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
    }
}
