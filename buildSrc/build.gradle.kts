plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.cqfn.diktat:diktat-gradle-plugin:1.1.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0-RC1")
}
