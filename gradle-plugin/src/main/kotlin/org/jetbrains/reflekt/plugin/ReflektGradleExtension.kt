package org.jetbrains.reflekt.plugin

import org.gradle.api.Project

/**
 * Users can configure this extension in their build.gradle like this:
 * reflekt {
 *   enabled = false
 *   // ... set other members on the ReflektGradleExtension class
 * }
 */
@PublishedApi
internal val Project.reflekt: ReflektGradleExtension
    get() = project.extensions.findByType(ReflektGradleExtension::class.java) ?: run {
        extensions.create("reflekt", ReflektGradleExtension::class.java)
    }

/**
 * Gradle extension class containing the configuration information for the plugin
 */
open class ReflektGradleExtension {
    /** If `false`, this plugin won't actually be applied */
    var enabled: Boolean = true

    /**
     * Path to which code should be generated.
     *
     * It would be automatically added to source set and marked
     * as generated in IntelliJ IDEA
     */
    var generationPath: String = "build/src/main/kotlin-gen"

    /** If `false`, Reflekt usages will not be saved into META-INF.
     * This information will be used if the current project is included
     * as a library for ReflektImpl file generation. */
    var toSaveMetadata: Boolean = false
}

/**
 * Reflekt Generator configuration extension.
 *
 * @param configure
 */
inline fun Project.reflekt(configure: ReflektGradleExtension.() -> Unit) {
    reflekt.apply(configure)
}
