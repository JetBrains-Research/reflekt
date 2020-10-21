import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.reflekt"
version = "0.1.0"

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    kotlin("jvm") version "1.3.72" apply true
    id("com.gradle.plugin-publish") version "0.11.0" apply false
}

allprojects {
    apply {
        plugin("tanvd.kosogor")
        plugin("kotlin")
    }

    repositories {
        jcenter()
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"
        }
    }
}
