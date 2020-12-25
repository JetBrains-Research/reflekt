import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("reflect"))
    api("com.google.devtools.ksp:symbol-processing-api:1.4.20-dev-experimental-20201204")
}

publishJar {
    publication {
        artifactId = "io.reflekt.dsl"
    }
}
