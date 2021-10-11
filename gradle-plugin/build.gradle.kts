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

pluginBundle {
    website = "https://github.com/JetBrains-Research/reflekt"
    vcsUrl = "https://github.com/JetBrains-Research/reflekt"
    tags = listOf("kotlin", "reflection", "reflekt")

    mavenCoordinates {
        groupId = project.group as String
        artifactId = "reflekt-plugin"
        version = project.version as String
    }
}

gradlePlugin {
    plugins {
        create("Reflekt") {
            id = "org.jetbrains.reflekt"
            displayName = "Reflekt"
            implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
            description = "Compile-time reflection library"
        }
    }
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







