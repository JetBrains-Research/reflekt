group = rootProject.group
version = rootProject.version

plugins {
    kotlin("jvm")
}


dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.lingala.zip4j", "zip4j", "2.6.1")
}
