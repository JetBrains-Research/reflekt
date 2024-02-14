plugins {
    id("org.jetbrains.reflekt.conventions")
}

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("compiler", version = libs.versions.kotlin.get()))
}
