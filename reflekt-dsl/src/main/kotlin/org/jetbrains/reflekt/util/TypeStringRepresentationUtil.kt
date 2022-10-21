package org.jetbrains.reflekt.util

import org.jetbrains.reflekt.InternalReflektApi
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter

/*
* This object has util functions for converting in the same String representation
* for different types in Kotlin compiler, e.g. KType, KotlinType
* */
@InternalReflektApi
public object TypeStringRepresentationUtil {
    private const val SEPARATOR = ", "
    private const val NULLABLE_SYMBOL = "?"
    public const val STAR_SYMBOL: String = "*"

    public fun getStringRepresentation(classifierName: String, arguments: List<String>): String {
        val argumentsStr = if (arguments.isNotEmpty()) {
            "<${arguments.joinToString(separator = SEPARATOR)}>"
        } else {
            ""
        }
        return "$classifierName$argumentsStr"
    }

    public fun markAsNullable(type: String, isNullable: Boolean): String = if (isNullable) {
        "$type$NULLABLE_SYMBOL"
    } else {
        type
    }
}

/**
 * @return human-readable string for this [KType]
 */
@InternalReflektApi
public fun KType.stringRepresentation(): String {
    // Get simple classifier name, e.g. kotlin.Function1
    val classifierName = (classifier as? KClass<*>)?.qualifiedName ?: (classifier as? KTypeParameter)?.name ?: ""
    // If type is null it means we have star projection
    return TypeStringRepresentationUtil.getStringRepresentation(
        classifierName,
        arguments.map { projection ->
            projection.type?.let { type ->
                TypeStringRepresentationUtil.markAsNullable(type.stringRepresentation(), type.isMarkedNullable)
            } ?: projection.toString()
        },
    )
}

/**
 * @param classifierName
 * @return human-readable string for `this` [KType] and classifier name
 */
@InternalReflektApi
internal fun KType.stringRepresentation(classifierName: String) =
    TypeStringRepresentationUtil.getStringRepresentation(classifierName, arguments.mapNotNull { it.type?.stringRepresentation() })
