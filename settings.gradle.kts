rootProject.name = "reflekt"

include(
    ":reflekt-core",
    ":reflekt-dsl",
    ":reflekt-plugin",
    ":gradle-plugin",
)

apply(from = "./buildSrc/repositories.settings.gradle.kts")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
