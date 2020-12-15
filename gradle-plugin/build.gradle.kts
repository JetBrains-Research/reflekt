import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("gradle-plugin-api", "1.4.20"))
    implementation(project(":reflekt-core"))
    api(project(":reflekt-dsl"))
    implementation(kotlin("compiler-embeddable:1.4.20"))
}

publishPlugin {
    id = "io.reflekt"
    displayName = "Reflekt"
    implementationClass = "io.reflekt.plugin.ReflektPlugin"
    version = project.version.toString()
}

publishJar {
    publication {
        artifactId = "io.reflekt.gradle-plugin"
    }
}
