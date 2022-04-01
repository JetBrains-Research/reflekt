import org.jetbrains.reflekt.plugin.reflekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = rootProject.group
version = rootProject.version

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    id("org.jetbrains.reflekt") version "1.5.31" apply true
    kotlin("jvm") version "1.5.31" apply true
}

allprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("org.jetbrains.reflekt")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            useIR = true
            languageVersion = "1.5"
            apiVersion = "1.5"
            jvmTarget = "11"
            // Current Reflekt version does not support incremental compilation process
            incremental = false
        }
    }

    dependencies {
        implementation("org.jetbrains.reflekt", "reflekt-dsl", "1.5.31")
        implementation("com.github.gumtreediff", "core", "2.1.2")
    }

    repositories {
        mavenCentral()
        google()
        mavenLocal()
        // Uncomment to use a released version
//         maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
    }

    reflekt {
        enabled = true
        // Use DependencyHandlers which have canBeResolve = True
        librariesToIntrospect = listOf()
//        toSaveMetadata = true
    }

}
