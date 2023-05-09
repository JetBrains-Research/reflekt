@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.plugin.publish)
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(embeddedKotlin("gradle-plugin-api"))
    implementation(projects.reflektCore)
    api(projects.reflektDsl)
    implementation(libs.kotlinx.serialization.protobuf)
}

gradlePlugin {
    website = "https://github.com/JetBrains-Research/reflekt"
    vcsUrl = "https://github.com/JetBrains-Research/reflekt"
    plugins {
        val reflekt by creating {
            id = "org.jetbrains.reflekt"
            displayName = "Gradle Reflekt plugin"
            description = rootProject.description
            implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
            tags.addAll(listOf("kotlin", "reflection", "reflekt"))
        }
    }
}
