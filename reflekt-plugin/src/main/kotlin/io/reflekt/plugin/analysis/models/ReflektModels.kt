package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.invokes.*
import io.reflekt.plugin.analysis.processor.uses.*
import io.reflekt.plugin.analysis.psi.function.toFunctionInfo
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

/*
 * If the function [withAnnotations] is called without subtypes then [subTypes] is [setOf(Any::class::qualifiedName)]
 * If the function [withSubTypes] is called without annotations then [annotations] is empty
 */
data class SubTypesToAnnotations(
    val subTypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

enum class ParameterizedTypeVariance {
    IN, OUT, STAR, INVARIANT
}

/*
* Recursive structure representing type that may have parameters
* For example, Map<Pair<Int, String>, Int> is represented in the following way:
* ParameterizedType(
*     "kotlin.collections.Map",
*     emptySet(),
*     listOf(
*         ParameterizedType(
*             "kotlin.Pair",
*             emptySet(),
*             listOf(
*                 ParameterizedType("kotlin.Int", emptySet(), emptyList(), ParameterizedTypeVariance.INVARIANT, false),
*                 ParameterizedType("kotlin.String", emptySet(), emptyList(), ParameterizedTypeVariance.INVARIANT, false)
*             ),
*             ParameterizedTypeVariance.INVARIANT,
*             false
*         ),
*         ParameterizedType("kotlin.Int", emptySet(), emptyList(), ParameterizedTypeVariance.INVARIANT, false)
*     ),
*     ParameterizedTypeVariance.INVARIANT,
*     false
* )
* */
data class ParameterizedType(
    val fqName: String,
    val superTypeFqNames: Set<String> = emptySet(),
    val parameters: List<ParameterizedType> = emptyList(),
    val variance: ParameterizedTypeVariance = ParameterizedTypeVariance.INVARIANT,
    val nullable: Boolean = false
) {
    fun withVariance(newVariance: ParameterizedTypeVariance): ParameterizedType =
        if (newVariance != ParameterizedTypeVariance.INVARIANT) copy(variance = newVariance) else this

    fun nullable(): ParameterizedType = copy(nullable = true)

    companion object {
        val STAR = ParameterizedType("", variance = ParameterizedTypeVariance.STAR)
    }
}

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

/* Stores enough information to generate function reference IR */
data class IrFunctionInfo(
    val fqName: String,
    val receiverFqName: String?,
    val isObjectReceiver: Boolean
)

typealias IrClassOrObjectUses = TypeUses<SubTypesToAnnotations, String>
typealias IrFunctionUses = TypeUses<SignatureToAnnotations, IrFunctionInfo>

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

data class IrReflektUses(
    val objects: IrClassOrObjectUses = HashMap(),
    val classes: IrClassOrObjectUses = HashMap(),
    val functions: IrFunctionUses = HashMap()
) {
    companion object {
        fun fromReflektUses(uses: ReflektUses, binding: BindingContext) = IrReflektUses(
            objects = uses.objects.mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() },
            classes = uses.classes.mapValues { (_, v) -> v.map { it.fqName!!.toString() }.toMutableList() },
            functions = uses.functions.mapValues { (_, v) -> v.map { it.toFunctionInfo(binding) }.toMutableList() }
        )
    }
}
