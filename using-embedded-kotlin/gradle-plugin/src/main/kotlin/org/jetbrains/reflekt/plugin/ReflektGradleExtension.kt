package org.jetbrains.reflekt.plugin

/**
 * Gradle extension class containing the configuration information for the plugin.
 */
open class ReflektGradleExtension {
    /** If `false`, this plugin won't actually be applied. */
    var enabled: Boolean = true

    /**
     * Path to which code should be generated.
     *
     * It would be automatically added to the source set and marked as generated in IntelliJ IDEA.
     */
    var generationPath: String = "build/src/main/kotlin-gen"

    /** If `false`, Reflekt usages will not be saved into META-INF.
     * This information will be used if the current project is included
     * as a library for ReflektImpl file generation. */
    var toSaveMetadata: Boolean = false
}
