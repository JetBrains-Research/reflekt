package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.reflekt.plugin.analysis.processor.FileId

import org.jetbrains.kotlin.types.Variance

import kotlinx.serialization.Serializable

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
 * @property fqName
 * @property arguments
 * @property returnType
 * @property receiverType
 */
@Serializable
data class SerializableKotlinType(
    val fqName: String,
    val arguments: List<SerializableTypeProjection> = emptyList(),
    val returnType: String,
    val receiverType: SerializableKotlinType?,
)

/**
 * @property classifierFqName
 * @property hasQuestionMark
 * @property arguments
 * @property annotations
 * @property abbreviation
 */
@Serializable
data class SerializableIrType(
    val classifierFqName: String,
    val hasQuestionMark: Boolean,
    val arguments: List<SerializableIrTypeArgument>,
    val annotations: List<SerializableIrType>,
    val abbreviation: String? = null,
)

/**
 * @property fqName
 * @property isStarProjection
 * @property variance
 */
@Serializable
data class SerializableIrTypeArgument(
    val fqName: String,
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

// TODO: think about a better name
interface Sizeable {
    fun isEmpty(): Boolean

    fun isNotEmpty() = !isEmpty()
}

/**
 * @property objects
 * @property classes
 * @property functions
 */
@Serializable
open class BaseCollectionReflektData<O : Collection<*>, C : Collection<*>, F : Collection<*>>(
    open val objects: O,
    open val classes: C,
    open val functions: F,
) : Sizeable {
    override fun isEmpty() = objects.isEmpty() && classes.isEmpty() && functions.isEmpty()
}

/**
 * @property objects
 * @property classes
 * @property functions
 */
open class BaseMapReflektData<O : HashMap<*, *>, C : HashMap<*, *>, F : HashMap<*, *>>(
    open val objects: O,
    open val classes: C,
    open val functions: F,
) : Sizeable {
    override fun isEmpty() = objects.isEmpty() && classes.isEmpty() && functions.isEmpty()
}

open class BaseReflektDataByFile<O : Any, C : Any, F : Any>(
    override val objects: HashMap<FileId, O>,
    override val classes: HashMap<FileId, C>,
    override val functions: HashMap<FileId, F>,
) : BaseMapReflektData<HashMap<FileId, O>, HashMap<FileId, C>, HashMap<FileId, F>>(
    objects,
    classes,
    functions)

@Suppress("IDENTIFIER_LENGTH")
fun <K : Any, V : Any, T : MutableCollection<V>> HashMap<K, T>.merge(second: HashMap<K, T>, defaultValue: () -> T): HashMap<K, T> =
    this.also { second.forEach { (k, v) -> this.getOrPut(k) { defaultValue() }.addAll(v) } }

fun <T> List<T>.merge(second: List<T>): List<T> = this.plus(second)

fun <T : Sizeable> merge(
    first: T,
    second: T,
    mergeFunction: (T, T) -> T,
): T =
    when {
        first.isEmpty() -> second
        second.isEmpty() -> first
        else -> mergeFunction(first, second)
    }
