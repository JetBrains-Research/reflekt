plugins {
    // id("tanvd.kosogor") //version "1.0.12" apply true
    // id("com.github.gmazzo.buildconfig") version "3.0.3" apply false
    org.jetbrains.reflekt.buildutils.`maven-publish-convention`
    org.jetbrains.reflekt.buildutils.`diktat-convention`
    // kotlin("kapt") version "1.5.31" apply true
    id("org.jetbrains.dokka")
    idea
    id("org.jetbrains.kotlinx.kover")
}

group = "org.jetbrains.reflekt"
/*
* To change version you should change the version in the following places:
*  - here (the main build.gradle.kts file)
*  - VERSION const in the Util.kt in the reflekt-core module
*  - VERSION const in the MavenLocalUtil object
*    class in tests in the reflekt-plugin module
*  - two places in the main README.md file (after realising)
*
* Also, you should change the version in two places in the build.gradle.kts file in the example project
*/
version = "1.5.31"

tasks.wrapper {
    gradleVersion = "7.4.1"
    distributionType = Wrapper.DistributionType.ALL
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

diktat {
    inputs += layout.projectDirectory.asFileTree.matching {
        include("buildSrc/**/*.kt")
        include("buildSrc/**/*.kts")
        exclude("buildSrc/build/**")

        // Diktat doesn't like the @Suppress in this file
        exclude("buildSrc/repositories.settings.gradle.kts")
    }
}
