package org.jetbrains.reflekt.plugin.analysis.models

import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.reflekt.plugin.analysis.processor.FileID


enum class ElementType(val value: String) {
    Block("BLOCK"),
    CallExpression("CALL_EXPRESSION"),
    DotQualifiedExpression("DOT_QUALIFIED_EXPRESSION"),
    File("kotlin.FILE"),
    FunctionLiteral("FUNCTION_LITERAL"),
    LambdaArgument("LAMBDA_ARGUMENT"),
    LambdaExpression("LAMBDA_EXPRESSION"),
    ReferenceExpression("REFERENCE_EXPRESSION"),
    TypeArgumentList("TYPE_ARGUMENT_LIST"),
    TypeProjection("TYPE_PROJECTION"),
    TypeReference("TYPE_REFERENCE"),
    ValueArgumentList("VALUE_ARGUMENT_LIST"),
    ValueParameterList("VALUE_PARAMETER_LIST"),
}

// TODO: think about a better name
interface Sizeable {
    fun isEmpty(): Boolean

    fun isNotEmpty() = !isEmpty()
}

open class BaseCollectionReflektData<O : Collection<*>, C : Collection<*>, F : Collection<*>>(
    open val objects: O,
    open val classes: C,
    open val functions: F
): Sizeable {
    override fun isEmpty() = objects.isEmpty() && classes.isEmpty() && functions.isEmpty()
}

open class BaseMapReflektData<O : HashMap<*, *>, C : HashMap<*, *>, F : HashMap<*, *>>(
    open val objects: O,
    open val classes: C,
    open val functions: F
): Sizeable {
    override fun isEmpty() = objects.isEmpty() && classes.isEmpty() && functions.isEmpty()
}

open class BaseReflektDataByFile<O : Any, C : Any, F : Any>(
    override val objects: HashMap<FileID, O>,
    override val classes: HashMap<FileID, C>,
    override val functions: HashMap<FileID, F>
) : BaseMapReflektData<HashMap<FileID, O>, HashMap<FileID, C>, HashMap<FileID, F>>(objects, classes, functions)

fun <K : Any, T : Iterable<*>> HashMap<K, T>.merge(second: HashMap<K, T>): HashMap<K, T> =
    this.also { second.forEach { (k, v) -> this.getOrPut(k) { v } } }

fun <T: Sizeable> merge(first: T, second: T, mergeFunction: (T, T) -> T): T {
    if (first.isEmpty()) return second
    if (second.isEmpty()) return first
    return mergeFunction(first, second)
}

@Serializable
data class SerializableKotlinType(
    val fqName: String,
    val arguments: List<SerializableTypeProjection> = emptyList(),
    val returnType: String,
    val receiverType: SerializableKotlinType?
)

@Serializable
data class SerializableTypeProjection(
    val fqName: String,
    val isStarProjection: Boolean,
    val projectionKind: Variance,
)
