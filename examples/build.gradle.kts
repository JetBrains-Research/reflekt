group = rootProject.group
version = rootProject.version

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    kotlin("jvm") version "1.3.72" apply true
    id("io.reflekt") version "0.1.0" apply true
}

repositories {
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.reflekt", "gradle-plugin", "0.1.0")
}

reflekt{
    enabled = true
    librariesToIntrospect = listOf("lalal")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
