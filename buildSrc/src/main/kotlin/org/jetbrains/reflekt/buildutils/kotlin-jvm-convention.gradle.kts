package org.jetbrains.reflekt.buildutils

import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.reflekt.buildutils.maven-publish-convention")
}

group = rootProject.group
version = rootProject.version


dependencies {
    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:${getKotlinPluginVersion()}"))

//    implementation(kotlin("reflect"))

    implementation("org.cqfn.diktat:diktat-gradle-plugin:1.0.0-rc.4")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0-RC1")
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
//        jvmTarget = "11"
//        languageVersion = "1.6"
//        apiVersion = "1.6"
        useIR = true
        languageVersion = "1.5"
        apiVersion = "1.5"
        jvmTarget = "11"
        // Current Reflekt version does not support incremental compilation process
        incremental = false
    }
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
    )
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
    }
}


//
//configureDiktat()
//configureDetekt()
