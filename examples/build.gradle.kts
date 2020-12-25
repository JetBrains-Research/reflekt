import io.reflekt.plugin.reflekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = rootProject.group
version = rootProject.version

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    id("io.reflekt") version "0.1.0" apply true
    kotlin("jvm") version "1.4.20" apply true
}

dependencies {
    implementation("io.reflekt", "gradle-plugin", "0.1.0")
    compileOnly("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
}

reflekt {
    enabled = true
    librariesToIntrospect = listOf("org.junit.jupiter:junit-jupiter-api:5.7.0")
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
    languageVersion = "1.4"
    apiVersion = "1.4"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
    languageVersion = "1.4"
    apiVersion = "1.4"
}
