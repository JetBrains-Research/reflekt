package io.reflekt.plugin

import org.gradle.api.Project

/**
 * Users can configure this extension in their build.gradle like this:
 * reflekt {
 *   enabled = false
 *   // ... set other members on the ReflektGradleExtension class
 * }
 */
@Suppress("CUSTOM_GETTERS_SETTERS")
internal val Project.reflekt: ReflektGradleExtension
    get() = project.extensions.findByType(ReflektGradleExtension::class.java) ?: run {
        extensions.create("reflekt", ReflektGradleExtension::class.java)
    }

/**
 * Gradle extension class containing the configuration information for the plugin
 */
open class ReflektGradleExtension {
    /** If [false], this plugin won't actually be applied */
    var enabled: Boolean = true

    /** Libraries to introspect, which are in the project dependencies in the format: "$group:$name:$version" */
    var librariesToIntrospect: List<String> = emptyList()

    /**
     * Path to which code should be generated.
     *
     * It would be automatically added to source set and marked
     * as generated in IntelliJ IDEA
     */
    var generationPath: String = "src/main/kotlin-gen"
}

/**
 * Reflekt Generator configuration extension.
 *
 * @param configure
 */
fun Project.reflekt(configure: ReflektGradleExtension.() -> Unit) {
    reflekt.apply(configure)
}
