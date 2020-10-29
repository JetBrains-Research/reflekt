import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler-embeddable"))
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin-api"))

    implementation("com.google.auto.service", "auto-service-annotations", "1.0-rc7")
    kapt("com.google.auto.service", "auto-service", "1.0-rc7")

    implementation(project(":reflekt-core"))
    implementation(project(":reflekt-dsl"))

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.7.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
    testImplementation("com.beust", "klaxon", "5.0.1")
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
