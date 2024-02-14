plugins {
    id("org.jetbrains.reflekt.conventions")
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(libs.tomlj)
}

tasks.processResources.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(rootProject.file("../gradle/libs.versions.toml"))
}
