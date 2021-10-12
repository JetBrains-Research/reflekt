package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.FileID
import io.reflekt.plugin.analysis.processor.source.invokes.*
import io.reflekt.plugin.analysis.processor.source.uses.*
import io.reflekt.plugin.analysis.psi.function.toFunctionInfo
import io.reflekt.plugin.analysis.serialization.SignatureToAnnotationsSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

/**
 * If the function [withAnnotations] is called without supertypes then [supertypes] is setOf(Any::class::qualifiedName)
 * If the function [withSupertypes] is called without annotations then [annotations] is empty
 */
@Serializable
data class SupertypesToAnnotations(
    val supertypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

@Serializable(with = SignatureToAnnotationsSerializer::class)
data class SignatureToAnnotations(
    var signature: KotlinType?, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet(),
    // We need to store fqName to deserialize KotlinType (only in invokes)
    val fqName: String? = null
)

typealias ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>

data class ReflektInvokes(
    val objects: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val classes: HashMap<FileID, ClassOrObjectInvokes> = HashMap(),
    val functions: HashMap<FileID, FunctionInvokes> = HashMap()
) {
    companion object {
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ObjectInvokesProcessor }.first().fileToInvokes,
            classes = processors.mapNotNull { it as? ClassInvokesProcessor }.first().fileToInvokes,
            functions = processors.mapNotNull { it as? FunctionInvokesProcessor }.first().fileToInvokes
        )
    }

    fun merge(second: ReflektInvokes): ReflektInvokes = ReflektInvokes(
        objects = this.objects.merge(second.objects),
        classes = this.classes.merge(second.classes),
        functions = this.functions.merge(second.functions)
    )

    private fun <V> HashMap<FileID, MutableSet<V>>.merge(second: HashMap<FileID, MutableSet<V>>): HashMap<FileID, MutableSet<V>> =
        this.also { second.forEach { (k, v) -> this.getOrPut(k) { v } } }
}

typealias TypeUses<K, V> = MutableMap<K, MutableList<V>>
typealias ClassOrObjectUses = TypeUses<SupertypesToAnnotations, KtClassOrObject>
typealias FunctionUses = TypeUses<SignatureToAnnotations, KtNamedFunction>

/* Stores enough information to generate function reference IR */
data class IrFunctionInfo(
    val fqName: String,
    val receiverFqName: String?,
    val isObjectReceiver: Boolean
)

typealias IrClassOrObjectUses = TypeUses<SupertypesToAnnotations, String>
typealias IrFunctionUses = TypeUses<SignatureToAnnotations, IrFunctionInfo>

fun ClassOrObjectUses.toSupertypesToFqNamesMap(): Map<Set<String>, List<String>> {
    return this.map { it.key.supertypes to it.value.mapNotNull { it.fqName?.toString() } }.toMap()
}

fun FunctionUses.toAnnotationsToFunction(): Map<Set<String>, List<KtNamedFunction>> {
    return this.map { it.key.annotations to it.value }.toMap()
}

/**
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
data class ReflektUses(
    val objects: HashMap<FileID, ClassOrObjectUses> = HashMap(),
    val classes: HashMap<FileID, ClassOrObjectUses> = HashMap(),
    val functions: HashMap<FileID, FunctionUses> = HashMap()
) {
    companion object {
        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().fileToUses,
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().fileToUses,
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().fileToUses
        )
    }
}

fun <T, V : KtElement> HashMap<FileID, TypeUses<T, V>>.flatten(): TypeUses<T, V> {
    val uses: TypeUses<T, V> = HashMap()
    this.forEach { (_, typeUses) ->
        typeUses.forEach { (k, v) ->
            uses.getOrPut(k) { mutableListOf() }.addAll(v)
        }
    }
    return uses
}

data class IrReflektUses(
    val objects: IrClassOrObjectUses = HashMap(),
    val classes: IrClassOrObjectUses = HashMap(),
    val functions: IrFunctionUses = HashMap()
) {
    companion object {
        fun fromReflektUses(uses: ReflektUses, binding: BindingContext) = IrReflektUses(
            objects = HashMap(uses.objects.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
            classes = HashMap(uses.classes.flatten().mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() }),
            functions = HashMap(uses.functions.flatten().mapValues { (_, v) -> v.map { it.toFunctionInfo(binding) }.toMutableList() }),
        )
    }

    fun merge(second: IrReflektUses): IrReflektUses = IrReflektUses(
        objects = this.objects.merge(second.objects),
        classes = this.classes.merge(second.classes),
        functions = this.functions.merge(second.functions)
    )

    private fun <K, V> TypeUses<K, V>.merge(second: TypeUses<K, V>): TypeUses<K, V> = this.also { second.forEach { (k, v) -> this.getOrPut(k) { v } } }
}
