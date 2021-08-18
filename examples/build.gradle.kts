import io.reflekt.plugin.reflekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = rootProject.group
version = rootProject.version

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    id("io.reflekt") version "0.1.0" apply true
    kotlin("jvm") version "1.5.10" apply true
}

allprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("io.reflekt")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            useIR = true
            languageVersion = "1.5"
            apiVersion = "1.5"
            jvmTarget = "11"
        }
    }

    dependencies {
        implementation("io.reflekt", "reflekt-dsl", "0.1.0")
        implementation("com.github.gumtreediff", "core", "2.1.2")
    }

    repositories {
        mavenCentral()
        google()
//        mavenLocal()
        maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
    }

    reflekt {
        enabled = true
        // Use DependencyHandlers which have canBeResolve = True
        librariesToIntrospect = listOf()
    }

}
