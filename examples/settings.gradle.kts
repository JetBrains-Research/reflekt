rootProject.name = "example"

include(":first-module")
include(":second-module")

pluginManagement {
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
        //add the dependency to Reflekt Maven repository
//        maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
    }
}
