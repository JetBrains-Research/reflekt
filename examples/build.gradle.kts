import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

group = rootProject.group
version = rootProject.version

plugins {
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("jvm") version "1.4.20" apply true
    id("io.reflekt") version "0.1.0" apply true
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.reflekt", "gradle-plugin", "0.1.0")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
}

reflekt {
    enabled = true
    librariesToIntrospect = listOf("org.junit.jupiter:junit-jupiter-api:5.7.0")
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "11"
        languageVersion = "1.4"
        apiVersion = "1.4"
    }
}

