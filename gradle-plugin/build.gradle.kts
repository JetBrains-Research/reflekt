import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.gradle.plugin-publish")
    id("com.github.gmazzo.buildconfig")
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
    implementation(project(":reflekt-core"))
    api(project(":reflekt-dsl"))

    implementation("com.google.auto.service", "auto-service-annotations", "1.0-rc7")
    kapt("com.google.auto.service", "auto-service", "1.0-rc7")
}

buildConfig {
    val project = project(":reflekt-plugin")
    packageName(project.group.toString())
    buildConfigField("String", "PLUGIN_GROUP_ID", "\"${project.group}\"")
    buildConfigField("String", "PLUGIN_ARTIFACT_ID", "\"${project.name}\"")
    buildConfigField("String", "PLUGIN_VERSION", "\"${project.version}\"")
}

pluginBundle {
    website = "https://github.com/nbirillo/reflekt"
    vcsUrl = "https://github.com/nbirillo/reflekt.git"
    tags = listOf("kotlin", "reflekt")
}

gradlePlugin {
    plugins {
        create("Reflekt") {
            id = "io.reflekt"
            displayName = "Reflekt"
            description = "Reflekt is a compile-time reflection library that leverages flows of standard reflection approach."
            implementationClass = "io.reflekt.plugin.ReflektPlugin"
        }
    }
}

publishPlugin {
    id = "io.reflekt.gradle"
    displayName = "Reflekt"
    implementationClass = "io.reflekt.plugin.ReflektPlugin"
    version = project.version.toString()
}

publishJar {
    publication {
        artifactId = "io.reflekt.gradle"
    }
}