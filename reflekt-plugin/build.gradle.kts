plugins {
    id("org.jetbrains.reflekt.conventions")
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("scripting-jvm-host-unshaded"))

    api("org.jetbrains.reflekt:reflekt-core:$version")
    api("org.jetbrains.reflekt:reflekt-dsl:$version")

    api(libs.kotlinpoet)
    api(libs.reflections)

    api(libs.kotlinx.serialization.protobuf)

    testRuntimeOnly(kotlin("test"))
    testRuntimeOnly(kotlin("script-runtime"))
    testRuntimeOnly(kotlin("annotations-jvm"))

    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("compiler-internal-test-framework"))

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
        setLibraryProperty("org.jetbrains.kotlin.compiler", "kotlin-compiler")
    }
}

tasks.processTestResources.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(rootProject.file("gradle/libs.versions.toml"))
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
    val path = project.configurations.testRuntimeClasspath
        .get()
        .files
        .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
        ?.absolutePath
        ?: return
    systemProperty(propName, path)
}

tasks.create<JavaExec>("generateTests") {
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass = "org.jetbrains.reflekt.plugin.compiler.GenerateTestsKt"
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
