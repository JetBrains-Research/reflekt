package org.jetbrains.reflekt.buildutils

plugins {
    id("org.cqfn.diktat.diktat-gradle-plugin")
}

diktat {
    diktatConfigFile = rootProject.file("diktat-analysis.yml")
    inputs = project.layout.projectDirectory.asFileTree.matching {
        include("*.kt")
        include("*.kts")

        // don't lint the test files
        exclude("src/test/resources/**/*.kt")
    }

    // inputs {
    // include("src/**/*.kt")
    // exclude("src/test/resources/**/*.kt")
    // }
    debug = true
}
