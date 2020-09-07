import io.reflekt.gradle.Versions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import tanvd.kosogor.accessors.jar
import tanvd.kosogor.proxy.shadowJar

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("org.jetbrains.kotlin", "kotlin-compiler-embeddable", Versions.kotlin)
    compileOnly("io.arrow-kt", "arrow-annotations", Versions.arrowAnnotations)

    // To run :create-plugin:shadowJar when building this project
    api(project(path = ":create-plugin", configuration = "shadow"))
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xplugin=${project.rootDir}/build/create-plugin/libs/create-plugin-all.jar")
    }
}
