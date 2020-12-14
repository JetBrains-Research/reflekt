package io.reflekt.plugin

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
