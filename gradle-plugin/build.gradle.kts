//import tanvd.kosogor.proxy.publishJar
//import tanvd.kosogor.proxy.publishPlugin

plugins {
    org.jetbrains.reflekt.buildutils.`kotlin-jvm-convention`
    `java-gradle-plugin`
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(projects.reflektCore)
    api(projects.reflektDsl)
    implementation(kotlin("compiler-embeddable"))
}

//publishPlugin {
//    id = "org.jetbrains.reflekt"
//    displayName = "Reflekt"
//    implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
//    version = project.version.toString()
//
//    info {
//        description = "Compile-time reflection library"
//        website = "https://github.com/JetBrains-Research/reflekt"
//        vcsUrl = "https://github.com/JetBrains-Research/reflekt"
//        tags.addAll(listOf("kotlin", "reflection", "reflekt"))
//    }
//}
//
//publishJar {}

gradlePlugin {
    plugins {
        create("Reflekt") {
            id = "org.jetbrains.reflekt"
            implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
        }
    }
}
