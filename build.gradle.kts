import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    id("com.github.johnrengelman.shadow") version "6.0.0" apply true
    kotlin("jvm") version "1.3.61" apply true
}

allprojects {
    apply {
        plugin("tanvd.kosogor")
        plugin("kotlin")
        plugin("com.github.johnrengelman.shadow")
    }

    repositories {
        jcenter()
        maven { setUrl("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.3"
            apiVersion = "1.3"
        }
    }
}
