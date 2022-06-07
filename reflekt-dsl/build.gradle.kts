import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version

dependencies {
    api(kotlin("compiler-embeddable"))
}

publishJar {}

tasks.withType<KotlinCompile<*>> {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}
