import org.jetbrains.reflekt.plugin.reflekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = rootProject.group
version = rootProject.version

plugins {
    id("org.jetbrains.reflekt") version "1.8.0-dev-6"
    id("org.jetbrains.kotlin.jvm") version "1.8.0-dev-6"
}

allprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
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
        implementation("org.jetbrains.reflekt", "reflekt-dsl", "1.8.0-dev-6")
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        // Uncomment to use a released version
//         maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
    }

    reflekt {
        enabled = true
    }

}
