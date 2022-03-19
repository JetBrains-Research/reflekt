// Repositories definitions that will be applied to both the buildSrc and main Gradle builds.

@Suppress("UnstableApiUsage") // centralised repository definitions are incubating
dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        jetbrainsSpace()
    }

    pluginManagement {
        repositories {
            gradlePluginPortal()
            mavenCentral()
            jetbrainsSpace()
        }
    }
}

/**
 * The Jetbrains Space Maven repository.
 *
 * This is disabled by default. To enable it, enable [enableJetbrainsSpaceRepo] by...
 *
 * * Command line property:
 *     ```shell
 *     ./gradlew build -PenableJetbrainsSpaceRepo=true
 *     ````
 * * Environment variable:
 *     ```env
 *     ORG_GRADLE_PROJECT_enableJetbrainsSpaceRepo=true
 *     ````
 * * or in `$GRADLE_HOME/gradle.properties`, or `<project-root>/gradle.properties`
 *     ```properties
 *     enableJetbrainsSpaceRepo=true
 *     ```
 *
 * [The full list of builds can be found here](https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_BuildNumber?mode=builds&tag=bootstrap).
 * (see builds with `<boostrap>` tag).
 *
 * @see [enableJetbrainsSpaceRepo]
 */
fun RepositoryHandler.jetbrainsSpace() {
    if (enableJetbrainsSpaceRepo().get()) {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }
}

/** @see [jetbrainsSpace] */
fun enableJetbrainsSpaceRepo(): Provider<Boolean> =
    providers.gradleProperty("enableJetbrainsSpaceRepo")
        .map { it.toBoolean() }
        .orElse(false)
