package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.invokes.*
import io.reflekt.plugin.analysis.processor.uses.*
import io.reflekt.plugin.analysis.psi.function.toFunctionInfo
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

/**
 * If the function [withAnnotations] is called without supertypes then [supertypes] is setOf(Any::class::qualifiedName)
 * If the function [withSupertypes] is called without annotations then [annotations] is empty
 */
data class SupertypesToAnnotations(
    val supertypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

data class SignatureToAnnotations(
    val signature: KotlinType, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet()
)

typealias ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>

data class ReflektInvokes(
    val objects: HashMap<String, ClassOrObjectInvokes> = HashMap(),
    val classes: HashMap<String, ClassOrObjectInvokes> = HashMap(),
    val functions: HashMap<String, FunctionInvokes> = HashMap()
) {
    companion object {
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ObjectInvokesProcessor }.first().fileToInvokes,
            classes = processors.mapNotNull { it as? ClassInvokesProcessor }.first().fileToInvokes,
            functions = processors.mapNotNull { it as? FunctionInvokesProcessor }.first().fileToInvokes
        )
    }
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
    val objects: HashMap<String, ClassOrObjectUses> = HashMap(),
    val classes: HashMap<String, ClassOrObjectUses> = HashMap(),
    val functions: HashMap<String, FunctionUses> = HashMap()
) {
    companion object {
        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().fileToUses,
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().fileToUses,
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().fileToUses
        )
    }
}

fun <T, V : KtElement> HashMap<String, TypeUses<T, V>>.flatten(): TypeUses<T, V> {
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
}
