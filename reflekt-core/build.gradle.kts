import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(kotlin("stdlib"))
    implementation("net.lingala.zip4j", "zip4j", "2.6.1")
}

publishJar {
    publication {
        artifactId = "io.reflekt.core"
    }
}
