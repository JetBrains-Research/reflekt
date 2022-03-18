@Suppress("UnstableApiUsage") // centralised repository definitions are incubating
dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        jetbrains()
    }

    pluginManagement {
        repositories {
            gradlePluginPortal()
            mavenCentral()
            jetbrains()
        }
    }
}


/**
 * [enable] this to use latest Kotlin compiler version.
 * The full list of builds can be found here:
 * https://teamcity.jetbrains.com/buildConfiguration/Kotlin_KotlinPublic_BuildNumber?mode=builds&tag=bootstrap
 * (see builds with <boostrap> tag).
 */
fun RepositoryHandler.jetbrains(enable: Boolean = false) {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}
