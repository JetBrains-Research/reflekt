package io.reflekt.plugin.analysis

import io.reflekt.Reflekt
import kotlin.reflect.full.declaredMemberProperties

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
            // Todo: Can I get full name automatically?
            return FunctionsFqNames(
                "${Reflekt.Objects::class.qualifiedName}.withSubType",
                "${Reflekt.Classes::class.qualifiedName}.withSubType",
                "${Reflekt.Objects.WithSubType::class.qualifiedName}.withAnnotation",
                "${Reflekt.Classes.WithSubType::class.qualifiedName}.withAnnotation"
            )
        }
    }

    val names: List<String>
        get() = listOf(withSubTypeObjects, withSubTypeClasses, withAnnotationObjects, withAnnotationClasses)
}

data class Invokes(
    //TODO-birillo please, use `MutableSet()` or `HashSet` instead of mutableSetOf(). *Of() constructors should not be used for creation of empty arrays
    val withSubTypeObjects: MutableSet<String> = mutableSetOf(),
    val withSubTypeClasses: MutableSet<String> = mutableSetOf(),
    val withAnnotationObjects: MutableMap<String, MutableList<String>> = mutableMapOf(),
    val withAnnotationClasses: MutableMap<String, MutableList<String>> = mutableMapOf()
)
