package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.kotlin.types.Variance
import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.ir.types.SimpleTypeNullability

/**
 * @property value
 */
enum class ElementType(val value: String) {
    BLOCK("BLOCK"),
    CALL_EXPRESSION("CALL_EXPRESSION"),
    DOT_QUALIFIED_EXPRESSION("DOT_QUALIFIED_EXPRESSION"),
    FILE("kotlin.FILE"),
    FUNCTION_LITERAL("FUNCTION_LITERAL"),
    FUNCTION_TYPE("FUNCTION_TYPE"),
    LAMBDA_ARGUMENT("LAMBDA_ARGUMENT"),
    LAMBDA_EXPRESSION("LAMBDA_EXPRESSION"),
    NULLABLE_TYPE("NULLABLE_TYPE"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    TYPE_PROJECTION("TYPE_PROJECTION"),
    TYPE_REFERENCE("TYPE_REFERENCE"),
    USER_TYPE("USER_TYPE"),
    VALUE_ARGUMENT_LIST("VALUE_ARGUMENT_LIST"),
    VALUE_PARAMETER("VALUE_PARAMETER"),
    VALUE_PARAMETER_LIST("VALUE_PARAMETER_LIST"),
    ;
}

/**
 * Interface of classes for which empty instances may exist.
 */
interface Emptiable {
    /**
     * The definition of "emptiness" may vary between implementations.
     *
     * @return `true` if the instance is empty, `false` otherwise.
     */
    fun isEmpty(): Boolean
}

/**
 * @property fqName
 * @property arguments
 * @property returnType
 * @property receiverType
 * @property contextReceiverTypes
 */
@Serializable
data class SerializableKotlinType(
    val fqName: String,
    val arguments: List<SerializableTypeProjection> = emptyList(),
    val returnType: String,
    val receiverType: SerializableKotlinType?,
    val contextReceiverTypes: List<SerializableKotlinType> = emptyList(),
)

/**
 * @property classifierFqName
 * @property nullability
 * @property arguments
 * @property annotations
 * @property abbreviation
 * @property variance
 */
@Serializable
data class SerializableIrType(
    val classifierFqName: String,
    val nullability: SimpleTypeNullability,
    val arguments: List<SerializableIrTypeArgument>,
    val annotations: List<SerializableIrType>,
    val abbreviation: String? = null,
    val variance: Variance,
)

/**
 * @property fqName
 * @property isStarProjection
 * @property variance
 */
@Serializable
data class SerializableIrTypeArgument(
    val fqName: String? = null,
    val isStarProjection: Boolean,
    val variance: Variance,
)

/**
 * @property fqName
 * @property isStarProjection
 * @property projectionKind
 */
@Serializable
data class SerializableTypeProjection(
    val fqName: String,
    val isStarProjection: Boolean,
    val projectionKind: Variance,
)

/**
 * @property objects
 * @property classes
 * @property functions
 */
@Serializable
open class BaseCollectionReflektData<out O : Collection<*>, out C : Collection<*>, out F : Collection<*>>(
    open val objects: O,
    open val classes: C,
    open val functions: F,
) : Emptiable {
    override fun isEmpty() = objects.isEmpty() && classes.isEmpty() && functions.isEmpty()
}

/**
 * @property objects
 * @property classes
 * @property functions
 */
open class BaseMapReflektData<out O : MutableMap<*, *>, out C : MutableMap<*, *>, out F : MutableMap<*, *>>(
    open val objects: O,
    open val classes: C,
    open val functions: F,
) : Emptiable {
    override fun isEmpty() = objects.isEmpty() && classes.isEmpty() && functions.isEmpty()
}

open class BaseReflektDataByFile<O : Any, C : Any, F : Any>(
    override val objects: MutableMap<FileId, O>,
    override val classes: MutableMap<FileId, C>,
    override val functions: MutableMap<FileId, F>,
) : BaseMapReflektData<MutableMap<FileId, O>, MutableMap<FileId, C>, MutableMap<FileId, F>>(
    objects,
    classes,
    functions
)

/**
 * Returns `true` if the instance is not empty.
 */
fun Emptiable.isNotEmpty() = !isEmpty()

@Suppress("IDENTIFIER_LENGTH")
inline fun <K : Any, V : Any, T : MutableCollection<V>, M : MutableMap<K, T>> M.merge(second: Map<K, T>, defaultValue: () -> T): M =
    this.also { second.forEach { (k, v) -> this.getOrPut(k) { defaultValue() }.addAll(v) } }

inline fun <T : Emptiable> merge(
    first: T,
    second: T,
    mergeFunction: (T, T) -> T,
): T = when {
    first.isEmpty() -> second
    second.isEmpty() -> first
    else -> mergeFunction(first, second)
}
