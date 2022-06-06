import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

plugins {
    alias(libs.plugins.kotlin.plugin.serialization)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

dependencies {
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))

    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)

    implementation(project(":reflekt-core"))
    implementation(project(":reflekt-dsl"))

    testImplementation(gradleTestKit())

    implementation(libs.kotlinpoet)
    implementation(libs.reflections)

    implementation(libs.kotlinx.serialization.protobuf)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.tomlj)
    testImplementation(libs.kotlin.compile.testing)
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeTags = setOf("analysis", "scripting", "ir", "parametrizedType", "codegen", "ic")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.create("analysis", Test::class.java) {
    useJUnitPlatform {
        includeTags = setOf("analysis")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>> {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.processTestResources.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(rootProject.file("gradle/libs.versions.toml"))
}

publishJar {}
