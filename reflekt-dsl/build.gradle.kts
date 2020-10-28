import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("jvm")
}


dependencies {
    implementation(kotlin("stdlib"))
}

publishJar {
    publication {
        artifactId = "io.reflekt.dsl"
    }
}
