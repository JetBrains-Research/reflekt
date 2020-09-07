repositories {
    jcenter()
    gradlePluginPortal()
}

plugins {
    id("tanvd.kosogor") version "1.0.9" apply true
    `kotlin-dsl` apply true
}


dependencies {
    compileOnly(gradleApi())
}