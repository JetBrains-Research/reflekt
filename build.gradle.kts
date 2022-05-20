import org.jetbrains.reflekt.buildutils.*

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "org.jetbrains.reflekt"
version = libs.versions.kotlin.asProvider().get()

plugins {
    `maven-publish`
    alias(libs.plugins.kosogor)
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.dokka)
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

allprojects {
    apply {
        plugin("kotlin")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    repositories {
        mavenCentral()
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
    @Suppress("EMPTY_BLOCK_STRUCTURE_ERROR")
    tasks.withType<Test> {
        dependsOn(tasks.withType<PublishToMavenLocal> {})
    }

    configureDiktat()
    configureDetekt()
}

createDiktatTask()
createDetektTask()

subprojects {
    apply(plugin = "maven-publish")

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
