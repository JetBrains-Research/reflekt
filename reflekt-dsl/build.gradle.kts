import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("stdlib:1.4.20"))
    api(kotlin("reflect:1.4.20"))
}

publishJar {
    publication {
        artifactId = "io.reflekt.dsl"
    }
}
