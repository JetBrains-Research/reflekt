package io.reflekt.plugin.analysis

import io.reflekt.Reflekt

enum class ElementType(val value: String) {
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
    CALL_EXPRESSION("CALL_EXPRESSION")
}

data class FunctionsFqNames(
    val withSubTypeObjects: String,
    val withSubTypeClasses: String,
    val withAnnotationObjects: String,
    val withAnnotationClasses: String
) {
    companion object {
        fun getReflektNames(): FunctionsFqNames {
            // Todo-birillo: Can I get full name automatically?
            return FunctionsFqNames(
                "${Reflekt.Objects::class.qualifiedName}.withSubType",
                "${Reflekt.Classes::class.qualifiedName}.withSubType",
                "${Reflekt.Objects.WithSubTypes::class.qualifiedName}.withAnnotation",
                "${Reflekt.Classes.WithSubTypes::class.qualifiedName}.withAnnotation"
            )
        }
    }

    val names: List<String>
        get() = listOf(withSubTypeObjects, withSubTypeClasses, withAnnotationObjects, withAnnotationClasses)
}

data class Invokes(
    val withSubTypeObjects: MutableSet<String> = HashSet(),
    val withSubTypeClasses: MutableSet<String> = HashSet(),
    val withAnnotationObjects: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val withAnnotationClasses: MutableMap<String, MutableList<String>> = mutableMapOf()
)
