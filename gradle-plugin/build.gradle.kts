import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(project(":reflekt-core"))
    api(project(":reflekt-dsl"))
    implementation(kotlin("compiler-embeddable"))
}

publishPlugin {
    id = "io.reflekt"
    displayName = "Reflekt"
    implementationClass = "io.reflekt.plugin.ReflektSubPlugin"
    version = project.version.toString()

    info {
        description = "Compile-time reflection library"
        website = "https://github.com/JetBrains-Research/reflekt"
        vcsUrl = "https://github.com/JetBrains-Research/reflekt"
        tags.addAll(listOf("kotlin", "reflection", "reflekt"))
    }
}

publishJar {
    publication {
        artifactId = "io.reflekt.gradle-plugin"
    }
}
