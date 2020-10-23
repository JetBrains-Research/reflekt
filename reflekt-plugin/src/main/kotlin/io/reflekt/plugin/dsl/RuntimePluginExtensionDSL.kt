package io.reflekt.plugin.dsl

import org.gradle.api.Project
import java.io.File
import java.io.Serializable

@DslMarker
annotation class ReflektDSLTag

class ReflektPluginExtension : Serializable {
    var generationPath: File? = null
    val librariesToIntrospect = HashSet<String>()

    internal fun generationPathOrDefault(project: Project): File {
        if (generationPath != null) return generationPath!!
        val default = File(project.buildDir, "kotlin-gen")
        default.mkdirs()
        return default
    }
}

internal var reflekt = ReflektPluginExtension()

@ReflektDSLTag
fun reflekt(configure: ReflektPluginExtension.() -> Unit) {
    reflekt.configure()
}
