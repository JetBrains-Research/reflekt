import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.reflekt"
/*
* To change version you should change the version in the following places:
*  - here (the main build.gradle.kts file)
*  - VERSION const in the Util.kt in the reflekt-core module
*  - version argument in the getReflektProjectJars function in AnalysisSetupTest
*    class in tests in the reflekt-plugin module
*  - two places in the main README.md file
*
* Also, you should change the version in two places in the build.gradle.kts file in the example project
* */
version = "0.2.1"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("jvm") version "1.5.10" apply true
    id("com.github.gmazzo.buildconfig") version "2.0.2" apply false
    `maven-publish`
    kotlin("kapt") version "1.5.10" apply true
}

allprojects {
    apply {
        plugin("kotlin")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.5"
            apiVersion = "1.5"
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
                    username = System.getenv("JB_SPACE_CLIENT_ID")?.takeIf { it.isNotBlank() } ?: ""
                    password = System.getenv("JB_SPACE_CLIENT_SECRET")?.takeIf { it.isNotBlank() } ?: ""
                }
            }
        }
    }
}
