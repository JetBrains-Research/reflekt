import io.reflekt.buildutils.*

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.reflekt"
/*
* To change version you should change the version in the following places:
*  - here (the main build.gradle.kts file)
*  - VERSION const in the Util.kt in the reflekt-core module
*  - version argument in the getReflektProjectJars function in AnalysisSetupTest
*    class in tests in the reflekt-plugin module
*  - two places in the main README.md file (after realising)
*
* Also, you should change the version in two places in the build.gradle.kts file in the example project
* */
version = "1.5.30"

plugins {
    id("tanvd.kosogor") version "1.0.12" apply true
    kotlin("jvm") version "1.5.30" apply true
    id("com.github.gmazzo.buildconfig") version "3.0.3" apply false
    `maven-publish`
    kotlin("kapt") version "1.5.30" apply true
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
        // Uncomment it for using the last kotlin compiler version
        // The full list of the build can be found here:
        // https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_BuildNumber?mode=builds&tag=bootstrap
        // (see builds with <boostrap> tag)
        // Note: uncomment it also in the settings.gradle.kts
        // maven {
        // url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        // }
    }

    // We should publish the project in the local maven repository before the tests running
    @kotlin.Suppress("EMPTY_BLOCK_STRUCTURE_ERROR")
    tasks.withType<Test> {
        dependsOn(tasks.withType<PublishToMavenLocal> {})
    }

    configureDiktat()
    configureDetekt()
}

createDiktatTask()
createDetektTask()

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
