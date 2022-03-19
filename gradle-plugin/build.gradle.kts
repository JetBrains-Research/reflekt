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
    description = "Compile-time reflection library"
    website ="https://github.com/JetBrains-Research/reflekt"
    vcsUrl ="https://github.com/JetBrains-Research/reflekt"
    tags = listOf("kotlin", "reflection", "reflekt")
}

gradlePlugin {
    plugins {
        create("Reflekt") {
            id = "org.jetbrains.reflekt"
            displayName = "Reflekt"
            description = "Reflekt is a compile-time reflection library"
            implementationClass = "org.jetbrains.reflekt.plugin.ReflektSubPlugin"
        }
    }
}
