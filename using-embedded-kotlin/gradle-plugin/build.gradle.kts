plugins {
    kotlin("plugin.serialization") version embeddedKotlinVersion
    alias(libs.plugins.plugin.publish)
}

group = rootProject.group
version = rootProject.version

dependencies {
    compileOnly(embeddedKotlin("gradle-plugin-api"))
    implementation(projects.reflektCore)
    implementation(libs.kotlinx.serialization.protobuf)
}

gradlePlugin {
    website = "https://github.com/JetBrains-Research/reflekt"
    vcsUrl = "https://github.com/JetBrains-Research/reflekt"

    plugins.create("reflekt") {
        id = "org.jetbrains.reflekt"
        displayName = "Gradle Reflekt plugin"
        description = rootProject.description
        implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
        tags.addAll(listOf("kotlin", "reflection", "reflekt"))
    }
}
