import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = "io.reflekt"
version = "0.1.0"

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("jvm") version "1.3.72" apply true
    id("com.github.gmazzo.buildconfig") version "2.0.2" apply false
}

allprojects {
    apply {
        plugin("kotlin")
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.4"
            apiVersion = "1.4"
        }
    }

    repositories {
        jcenter()
    }

    // We should publish the project in the local maven repository before the tests running
    tasks.withType<Test> {
        dependsOn(tasks.withType<PublishToMavenLocal>{})
    }
}
