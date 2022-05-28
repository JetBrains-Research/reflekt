import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("kapt")
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(project(":reflekt-core"))
    api(project(":reflekt-dsl"))
    implementation(kotlin("compiler-embeddable"))
}

publishPlugin {
    id = "org.jetbrains.reflekt"
    displayName = "Reflekt"
    implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
    version = project.version.toString()

    info {
        description = "Compile-time reflection library"
        website = "https://github.com/JetBrains-Research/reflekt"
        vcsUrl = "https://github.com/JetBrains-Research/reflekt"
        tags.addAll(listOf("kotlin", "reflection", "reflekt"))
    }
}
