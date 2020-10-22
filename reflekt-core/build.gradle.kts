import tanvd.kosogor.accessors.jar
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    implementation("net.lingala.zip4j", "zip4j", "2.6.1")
}
