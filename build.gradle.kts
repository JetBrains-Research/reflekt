plugins {
    org.jetbrains.reflekt.buildutils.`maven-publish-convention`
    org.jetbrains.reflekt.buildutils.`diktat-convention`
    id("org.jetbrains.dokka")
    idea
    id("org.jetbrains.kotlinx.kover")
}

group = "org.jetbrains.reflekt"
/*
* To change version you should change the version in the following places:
*  - here (the main build.gradle.kts file)
*  - VERSION const in `reflekt-core/src/main/kotlin/org/jetbrains/reflekt/util/Util.kt`
*  - the main README.md file (after releasing)
*
* Also, you should change the version in `build.gradle.kts` file in the example project
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
