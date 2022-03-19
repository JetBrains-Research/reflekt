plugins {
    org.jetbrains.reflekt.buildutils.`kotlin-jvm-convention`

    kotlin("kapt")
    kotlin("plugin.serialization")
}

val reflektTestLibs: Configuration by configurations.creating {
    description = "Dependencies that Reflekt will analyse during testing"
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = false
}

dependencies {
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))

    implementation("com.google.auto.service", "auto-service-annotations", "1.0")
    kapt("com.google.auto.service", "auto-service", "1.0")

    implementation(projects.reflektCore)
    implementation(projects.reflektDsl)
    implementation(projects.gradlePlugin)
    implementation(kotlin("stdlib"))

    // explicit dependencies on libs that are used by Reflekt tests
    reflektTestLibs(projects.reflektCore)
    reflektTestLibs(projects.reflektDsl)
    reflektTestLibs(projects.gradlePlugin)
    reflektTestLibs(kotlin("stdlib"))

    testImplementation(gradleTestKit())

    implementation("com.squareup", "kotlinpoet", "1.9.0")
    implementation("org.reflections", "reflections", "0.9.12")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.2.0")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
    }
    testImplementation("com.github.tschuchortdev", "kotlin-compile-testing", "1.4.5")
}

val reflektPrepareTestDependencies by tasks.registering(Sync::class) {
    description = "Fetch libs from `reflektTestLibs` config and store them in `./build/reflekt-test-libs`"
    from(reflektTestLibs)
    into(project.layout.buildDirectory.dir("reflekt-test-libs"))
    doLast {
        logger.lifecycle("Fetched ${source.count()} Reflekt libs into $destinationDir")
    }
}

tasks.withType<Test> {
    dependsOn(reflektPrepareTestDependencies)

    doFirst("provideReflektTestLibsPath") {
        systemProperties(
            "reflektTestLibDir" to reflektPrepareTestDependencies.get().destinationDir.canonicalPath
        )
    }

    useJUnitPlatform {
        includeTags = setOf("analysis", "scripting", "ir", "parametrizedType", "codegen", "ic")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }
}

listOf("analysis", "ir").forEach { tag ->
    tasks.register("test-$tag", Test::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "run tests tagged with '$tag'"
        useJUnitPlatform { includeTags = setOf(tag) }
    }
}
