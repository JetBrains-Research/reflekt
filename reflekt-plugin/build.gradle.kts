import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

plugins {
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host-unshaded"))

    implementation(project(":reflekt-core"))
    implementation(project(":reflekt-dsl"))

    implementation(libs.kotlinpoet)
    implementation(libs.reflections)

    implementation(libs.kotlinx.serialization.protobuf)

    // TODO: disable runtime after deleting kotlin script
    kotlin("compiler")
//        .let {
//        compileOnly(it)
//        testImplementation(it)
//    }

    testRuntimeOnly(libs.kotlin.test)
    testRuntimeOnly(libs.kotlin.script.runtime)
    testRuntimeOnly(libs.kotlin.annotations.jvm)

    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.compiler.internal.test.framework)
    testImplementation("junit:junit:4.12")

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.commons)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.junit.platform.runner)
    testImplementation(libs.junit.platform.suite.api)

    testImplementation(libs.tomlj)
}

tasks.withType<Test> {
    useJUnitPlatform()
    workingDir = project.rootDir

    doFirst {
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
    }
}

tasks.processTestResources.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(rootProject.file("gradle/libs.versions.toml"))
}

publishJar {}

fun Test.setLibraryProperty(propName: String, jarName: String) {
    val path =
        project.configurations.testRuntimeClasspath
            .get()
            .files
            .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
            ?.absolutePath
            ?: return
    systemProperty(propName, path)
}

tasks.create<JavaExec>("generateTests") {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("org.jetbrains.reflekt.plugin.compiler.GenerateTestsKt")
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        java.setSrcDirs(listOf("src/test/java", "src/test/kotlin"))
        resources.setSrcDirs(listOf("resources"))
    }
}
