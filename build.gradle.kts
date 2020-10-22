import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    kotlin("jvm") version "1.3.72" apply true
    id("com.gradle.plugin-publish") version "0.11.0" apply false
    id("com.github.gmazzo.buildconfig") version "2.0.2" apply false
}

allprojects {
    group = "io.reflekt"
    version = "0.1.0"

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"
        }
    }

    repositories {
        mavenCentral()
        jcenter()
    }
}

