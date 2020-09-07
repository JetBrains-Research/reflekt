import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.reflekt.gradle.Versions

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("org.jetbrains.kotlin", "kotlin-compiler-embeddable", Versions.kotlin)
    implementation("io.arrow-kt", "compiler-plugin", Versions.arrowMeta)
}
