import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    implementation("net.lingala.zip4j", "zip4j", "2.9.0")
}



publishJar {}
