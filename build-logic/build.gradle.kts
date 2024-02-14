plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugin.kotlin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
