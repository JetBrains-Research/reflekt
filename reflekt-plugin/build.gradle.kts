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
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    compileOnly("com.google.auto.service", "auto-service", "1.0-rc4")
    compile(project(":reflekt-core"))

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.0")
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

