import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.5.31"
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:${getKotlinPluginVersion()}"))

    // Define the Maven coordinates (not Gradle plugin ID!) of gradle plugins here.

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${getKotlinPluginVersion()}")
    implementation("org.jetbrains.kotlin:kotlin-serialization:${getKotlinPluginVersion()}")

    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")

    implementation("org.jetbrains.kotlinx:kover:0.5.0")
    // implementation("tanvd.kosogor:kosogor:1.0.12")
    // implementation("gradle.plugin.tanvd.kosogor:kosogor:1.0.13")

    implementation("org.cqfn.diktat:diktat-gradle-plugin:1.0.0-rc.4")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0-RC1")

    implementation("com.gradle.publish:plugin-publish-plugin:0.20.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        languageVersion = "1.5"
        apiVersion = "1.5"
        jvmTarget = "11"
    }
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
    }

    kotlinDslPluginOptions {
        jvmTarget.set("11")
    }
}
