package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.invokes.*
import io.reflekt.plugin.analysis.processor.uses.*
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

/*
 * If the function [withAnnotations] is called without subtypes then [subTypes] is [setOf(Any::class::qualifiedName)]
 * If the function [withSubTypes] is called without annotations then [annotations] is empty
 */
data class SubTypesToAnnotations(
    val subTypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

data class ParameterizedType(
    val fqName: String,
    val parameters: List<ParameterizedType> = emptyList()
)

data class SignatureToAnnotations(
    val signature: ParameterizedType, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet()
)

typealias ClassOrObjectInvokes = MutableSet<SubTypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>

data class ReflektInvokes(
    val objects: ClassOrObjectInvokes = HashSet(),
    val classes: ClassOrObjectInvokes = HashSet(),
    val functions: FunctionInvokes = HashSet()
) {
    companion object{
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ObjectInvokesProcessor }.first().invokes,
            classes = processors.mapNotNull { it as? ClassInvokesProcessor }.first().invokes,
            functions = processors.mapNotNull { it as? FunctionInvokesProcessor }.first().invokes
        )
    }
}

typealias TypeUses<K, V> = Map<K, MutableList<V>>
typealias ClassOrObjectUses = TypeUses<SubTypesToAnnotations, KtClassOrObject>
typealias FunctionUses = TypeUses<SignatureToAnnotations, KtNamedFunction>

fun ClassOrObjectUses.toSubTypesToFqNamesMap(): Map<Set<String>, MutableList<KtClassOrObject>> {
    return this.map { it.key.subTypes to it.value }.toMap()
}

/*
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
data class ReflektUses(
    val objects: ClassOrObjectUses = HashMap(),
    val classes: ClassOrObjectUses = HashMap(),
    val functions: FunctionUses = HashMap()
) {
    companion object{
        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().uses,
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().uses,
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().uses
        )
    }
}
