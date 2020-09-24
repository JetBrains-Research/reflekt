package io.reflekt.plugin.analysis

import io.reflekt.Reflekt

enum class ElementType(val value: String) {
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
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
                "${Reflekt.Objects::class.qualifiedName}.withSubType.withAnnotation",
                "${Reflekt.Classes::class.qualifiedName}.withSubType.withAnnotation"
            )
        }
    }
}
