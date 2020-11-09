import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

plugins {
    kotlin("jvm")
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler-embeddable"))
}

publishJar {
    publication {
        artifactId = "io.reflekt.dsl"
    }
}
