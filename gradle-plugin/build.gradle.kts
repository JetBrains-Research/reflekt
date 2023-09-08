import tanvd.kosogor.proxy.publishPlugin

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(projects.reflektCore)
    api(projects.reflektDsl)
    implementation(libs.kotlinx.serialization.protobuf)
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
