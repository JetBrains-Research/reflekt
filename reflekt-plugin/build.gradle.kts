//import tanvd.kosogor.proxy.publishJar


plugins {
    org.jetbrains.reflekt.buildutils.`kotlin-jvm-convention`

    kotlin("kapt")
    kotlin("plugin.serialization")// version "1.5.31" apply true
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

    testImplementation(gradleTestKit())

    implementation("com.squareup", "kotlinpoet", "1.9.0")
    implementation("org.reflections", "reflections", "0.9.12")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.2.0")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("Only needed to run tests in a version of IntelliJ IDEA that bundles older versions")
    }
//    testImplementation("com.google.code.gson", "gson", "2.8.8")
    testImplementation("com.github.tschuchortdev", "kotlin-compile-testing", "1.4.7")
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

//publishJar {}
