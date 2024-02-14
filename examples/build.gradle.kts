import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = rootProject.group
version = rootProject.version

plugins {
    id("org.jetbrains.reflekt") version "1.9.22"
    kotlin("jvm") version "1.9.22"
}

allprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.reflekt")
    }

    java {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            // Current Reflekt version does not support incremental compilation process
            incremental = false
        }
    }

    dependencies {
        implementation("org.jetbrains.reflekt", "reflekt-dsl", "1.9.22")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        // Uncomment to use a released version
        // maven("https://packages.jetbrains.team/maven/p/reflekt/reflekt")
    }
}
