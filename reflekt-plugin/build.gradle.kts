import tanvd.kosogor.proxy.publishJar
import tanvd.kosogor.proxy.publishPlugin

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    api(project(":reflekt"))

    implementation(kotlin("compiler-embeddable"))

    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin-api"))

    implementation("com.squareup", "kotlinpoet", "1.6.0")
}

publishPlugin {
    id = "io.reflekt"
    displayName = "Reflekt"
    implementationClass = "io.reflekt.plugin.ReflektPlugin"
    version = project.version.toString()
}

publishJar {
    publication {
        artifactId = "io.reflekt.gradle.plugin"
    }
}
