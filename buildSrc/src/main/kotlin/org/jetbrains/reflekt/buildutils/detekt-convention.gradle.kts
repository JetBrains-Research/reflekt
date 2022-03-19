package org.jetbrains.reflekt.buildutils

plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    config.from(rootProject.file("detekt.yml"))
    buildUponDefaultConfig = true
    debug = true
}
