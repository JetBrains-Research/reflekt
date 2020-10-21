package io.reflekt.plugin

open class ReflektGradleExtension {
    /** If [false], this plugin won't actually be applied */
    var enabled: Boolean = true

    /** Libraries to introspect, which are in the project dependencies in the format: "$group:$name:$version" */
    var librariesToIntrospect: List<String> = emptyList()
}
