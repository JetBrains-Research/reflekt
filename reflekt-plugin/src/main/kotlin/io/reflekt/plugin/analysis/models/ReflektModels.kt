package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.invokes.*
import io.reflekt.plugin.analysis.processor.uses.*
import io.reflekt.plugin.analysis.psi.function.toFunctionInfo
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

typealias ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>

typealias TypeUses<K, V> = MutableMap<K, MutableList<V>>
typealias ClassOrObjectUses = TypeUses<SupertypesToAnnotations, KtClassOrObject>
typealias FunctionUses = TypeUses<SignatureToAnnotations, KtNamedFunction>

typealias IrClassOrObjectUses = TypeUses<SupertypesToAnnotations, String>
typealias IrFunctionUses = TypeUses<SignatureToAnnotations, IrFunctionInfo>

/**
 * If the function [withAnnotations] is called without supertypes then [supertypes] is setOf(Any::class::qualifiedName)
 * If the function [withSupertypes] is called without annotations then [annotations] is empty
 * @property supertypes
 * @property annotations
 */
data class SupertypesToAnnotations(
    val supertypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet(),
)

/**
 * @property signature
 * @property annotations kotlin.FunctionN< ... >
 */
data class SignatureToAnnotations(
    val signature: KotlinType, val annotations: Set<String> = emptySet(),
)

/**
 * @property objects
 * @property classes
 * @property functions
 */
data class ReflektInvokes(
    val objects: HashMap<FileId, ClassOrObjectInvokes> = HashMap(),
    val classes: HashMap<FileId, ClassOrObjectInvokes> = HashMap(),
    val functions: HashMap<FileId, FunctionInvokes> = HashMap(),
) {
    companion object {
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ObjectInvokesProcessor }.first().fileToInvokes,
            classes = processors.mapNotNull { it as? ClassInvokesProcessor }.first().fileToInvokes,
            functions = processors.mapNotNull { it as? FunctionInvokesProcessor }.first().fileToInvokes,
        )
    }
}

/**
 * @property fqName
 * @property receiverFqName
 * @property isObjectReceiver
 */
/* Stores enough information to generate function reference IR */
data class IrFunctionInfo(
    val fqName: String,
    val receiverFqName: String?,
    val isObjectReceiver: Boolean,
)

/**
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 * @property objects
 * @property classes
 * @property functions
 */
data class ReflektUses(
    val objects: HashMap<FileId, ClassOrObjectUses> = HashMap(),
    val classes: HashMap<FileId, ClassOrObjectUses> = HashMap(),
    val functions: HashMap<FileId, FunctionUses> = HashMap(),
) {
    companion object {
        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().fileToUses,
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().fileToUses,
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().fileToUses,
        )
    }
}

/**
 * @property objects
 * @property classes
 * @property functions
 */
data class IrReflektUses(
    val objects: IrClassOrObjectUses = HashMap(),
    val classes: IrClassOrObjectUses = HashMap(),
    val functions: IrFunctionUses = HashMap(),
) {
    companion object {
        fun fromReflektUses(uses: ReflektUses, binding: BindingContext) = IrReflektUses(
            objects = HashMap(uses.objects.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
            classes = HashMap(uses.classes.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
            functions = HashMap(uses.functions.flatten().mapValues { (_, v) -> v.map { it.toFunctionInfo(binding) }.toMutableList() }),
        )
    }
}

fun ClassOrObjectUses.toSupertypesToFqNamesMap() = this.map { it.key.supertypes to it.value.mapNotNull { it.fqName?.toString() } }.toMap()

fun FunctionUses.toAnnotationsToFunction() = this.map { it.key.annotations to it.value }.toMap()

fun <T, V : KtElement> HashMap<FileId, TypeUses<T, V>>.flatten(): TypeUses<T, V> {
    val uses: TypeUses<T, V> = HashMap()
    this.forEach { (_, typeUses) ->
        typeUses.forEach { (k, v) ->
            uses.getOrPut(k) { mutableListOf() }.addAll(v)
        }
    }
    return uses
}
