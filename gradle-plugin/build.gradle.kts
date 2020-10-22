import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

plugins {
    `java-gradle-plugin`
    id("org.jetbrains.kotlin.jvm")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("compiler-embeddable"))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin-api"))
    compileOnly("com.google.auto.service", "auto-service", "1.0-rc4")
    compile(project(":reflekt-core"))
}

publishPlugin {
    id = "reflekt"
    displayName = "Reflekt"
    implementationClass = "io.reflekt.plugin.ReflektPlugin"
    version = project.version.toString()
}

publishJar {
    publication {
        artifactId = "io.reflekt.gradle.plugin"
    }
}
