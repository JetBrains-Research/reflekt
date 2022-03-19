import org.jetbrains.reflekt.buildutils.ProjectMetadata

plugins {
    org.jetbrains.reflekt.buildutils.`kotlin-jvm-convention`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(projects.reflektCore)
    api(projects.reflektDsl)
    implementation(kotlin("compiler-embeddable"))
}

pluginBundle {
    description = ProjectMetadata.description
    website = ProjectMetadata.homepage
    vcsUrl = ProjectMetadata.scmHomepage
    tags = listOf("kotlin", "reflection", "reflekt")
}

gradlePlugin {
    plugins {
        create("Reflekt") {
            id = "org.jetbrains.reflekt"
            displayName = "Reflekt"
            description = "Apply Reflekt compile-time reflection to a Gradle project"
            implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
        }
    }
}
