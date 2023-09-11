package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.kotlin.types.Variance
import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.ir.types.SimpleTypeNullability
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.reflekt.plugin.analysis.serialization.ClassIdSerializer

/**
 * Interface of classes for which empty instances may exist.
 */
interface Emptiable {
    /**
     * Returns `true` if the instance is empty, `false` otherwise. The definition of "emptiness" may vary between implementations.
     */
    fun isEmpty(): Boolean
}

/**
 *
 *
 * @property classifierClassId
 * @property nullability
 * @property arguments
 * @property annotations
 * @property abbreviation
 * @property variance
 */
@Serializable
data class SerializableIrType(
    val classifierClassId: @Serializable(with = ClassIdSerializer::class) ClassId,
    val nullability: SimpleTypeNullability,
    val arguments: List<SerializableIrTypeArgument>,
    val annotations: List<SerializableIrType>,
    val abbreviation: String? = null,
    val variance: Variance,
)

/**
 *
 *
 * @property classId The class ID of the serialized IrType
 * @property isStarProjection
 * @property variance
 */
@Serializable
data class SerializableIrTypeArgument(
    val classId: @Serializable(with = ClassIdSerializer::class) ClassId? = null,
    val isStarProjection: Boolean,
    val variance: Variance,
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
    this.apply { second.forEach { (k, v) -> getOrPut(k) { defaultValue() }.addAll(v) } }
