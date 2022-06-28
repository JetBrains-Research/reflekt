import org.jetbrains.reflekt.plugin.reflekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = rootProject.group
version = rootProject.version

plugins {
    id("tanvd.kosogor") version "1.0.13"
    id("org.jetbrains.reflekt") version "1.7.20-dev-2312"
    kotlin("jvm") version "1.7.20-dev-2312"
}

allprojects {
    apply {
        plugin("kotlin")
        plugin("tanvd.kosogor")
        plugin("org.jetbrains.reflekt")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            // Current Reflekt version does not support incremental compilation process
            incremental = false
        }
    }

    dependencies {
        implementation("org.jetbrains.reflekt", "reflekt-dsl", "1.7.20-dev-2312")
    }

    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        mavenLocal()
        // Uncomment to use a released version
//         maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
    }

    reflekt {
        enabled = true
    }

}
