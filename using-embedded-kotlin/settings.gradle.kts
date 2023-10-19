enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "using-embedded-kotlin"

include(
    ":reflekt-core",
    ":reflekt-dsl",
    ":gradle-plugin",
)

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement.versionCatalogs {
    create("libs") {
        from(files("../gradle/libs.versions.toml"))
    }
}
