package io.reflekt.util

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter

/*
* This object has util functions for converting in the same String representation
* for different types in Kotlin compiler, e.g. KType, KotlinType
* */
object TypeStringRepresentationUtil {
    private const val SEPARATOR = ", "
    private const val NULLABLE_SYMBOL = "?"
    const val STAR_SYMBOL = "*"

    fun getStringRepresentation(classifierName: String, arguments: List<String>): String {
        val argumentsStr = if (arguments.isNotEmpty()) {
            "<${arguments.joinToString(separator = SEPARATOR)}>"
        } else {
            ""
        }
        return "$classifierName$argumentsStr"
    }

    fun markAsNullable(type: String, isNullable: Boolean): String = if (isNullable) {
        "$type$NULLABLE_SYMBOL"
    } else {
        type
    }
}

/**
 * @return human readable string for `this` KType
 */
fun KType.stringRepresentation(): String {
    // Get simple classifier name, e.g. kotlin.Function1
    val classifierName = (classifier as? KClass<*>)?.qualifiedName ?: (classifier as? KTypeParameter)?.name ?: ""
    // If type is null it means we have star projection
    return TypeStringRepresentationUtil
        .getStringRepresentation(classifierName,
            arguments.map {
                it.type?.let { type ->
                    TypeStringRepresentationUtil.markAsNullable(type.stringRepresentation(), type.isMarkedNullable)
                } ?: it.toString()
            },
        )
}

/**
 * @param classifierName
 * @return human readable string for `this` KType and classifier name
 */
internal fun KType.stringRepresentation(classifierName: String) =
        TypeStringRepresentationUtil.getStringRepresentation(classifierName, arguments.mapNotNull { it.type?.stringRepresentation() })
