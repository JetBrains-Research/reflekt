import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("kapt")
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.kotlin.link")
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(project(":reflekt-core"))
    api(project(":reflekt-dsl"))
    implementation(kotlin("compiler-embeddable"))
}

gradlePlugin {
    plugins {
        create("Reflekt") {
            id = "org.jetbrains.reflekt"
            implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/JetBrains-Research/reflekt"
    vcsUrl = "https://github.com/JetBrains-Research/reflekt"


    (plugins) {
        "Reflekt" {
            displayName = "Reflekt"
            tags = listOf("kotlin", "reflection", "reflekt")
            version = project.version as String
            description = "Compile-time reflection library"

        }
    }
}

