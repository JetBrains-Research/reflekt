import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("compiler-embeddable", "1.4.20"))

    implementation("com.google.auto.service", "auto-service-annotations", "1.0-rc7")
    kapt("com.google.auto.service", "auto-service", "1.0-rc7")

    implementation(project(":reflekt-core"))
    implementation(project(":reflekt-dsl"))

    testImplementation(gradleTestKit())

    implementation("com.squareup", "kotlinpoet", "1.6.0")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.7.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
    testImplementation("com.google.code.gson", "gson", "2.8.6")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeTags = setOf("analysis")
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

publishJar {
    publication {
        artifactId = "reflekt-compiler-plugin"
    }
}
