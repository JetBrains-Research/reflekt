import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.reflekt"
version = "0.1.0"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("jvm") version "1.4.20" apply true
    id("com.github.gmazzo.buildconfig") version "2.0.2" apply false
    `maven-publish`
}

allprojects {
    apply {
        plugin("kotlin")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.4"
            apiVersion = "1.4"
        }
    }

    repositories {
        jcenter()
        google()
    }

    // We should publish the project in the local maven repository before the tests running
    tasks.withType<Test> {
        dependsOn(tasks.withType<PublishToMavenLocal>{})
    }
}

subprojects {
    apply {
        plugin("maven-publish")
    }

    publishing {
        repositories {
            maven {
                name = "SpacePackages"
                url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt")

                credentials {
                    username = properties.getOrDefault("JB_SPACE_CLIENT_ID", "").toString()
                    password = properties.getOrDefault("JB_SPACE_CLIENT_SECRET", "").toString()
                }
            }
        }
    }
}
