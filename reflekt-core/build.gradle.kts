import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(libs.zip4j)
    implementation(libs.tomlj)
}

publishJar {}

tasks.processResources.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(rootProject.file("gradle/libs.versions.toml"))
}
