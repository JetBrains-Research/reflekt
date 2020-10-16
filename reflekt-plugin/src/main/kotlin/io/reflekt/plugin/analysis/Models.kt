package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.processor.invokes.BaseInvokesProcessor
import io.reflekt.plugin.analysis.processor.invokes.ClassInvokesProcessor
import io.reflekt.plugin.analysis.processor.invokes.FunctionInvokesProcessor
import io.reflekt.plugin.analysis.processor.invokes.ObjectInvokesProcessor
import io.reflekt.plugin.analysis.processor.uses.BaseUsesProcessor
import io.reflekt.plugin.analysis.processor.uses.ClassUsesProcessor
import io.reflekt.plugin.analysis.processor.uses.FunctionUsesProcessor
import io.reflekt.plugin.analysis.processor.uses.ObjectUsesProcessor

enum class ElementType(val value: String) {
    TypeArgumentList("TYPE_ARGUMENT_LIST"),
    ReferenceExpression("REFERENCE_EXPRESSION"),
    CallExpression("CALL_EXPRESSION"),
    DotQualifiedExpression("DOT_QUALIFIED_EXPRESSION"),
    ValueArgumentList("VALUE_ARGUMENT_LIST")
}


/*
 * If the function [withAnnotations] is called without subtypes then [subTypes] is [setOf(Any::class::qualifiedName)]
 * If the function [withSubTypes] is called without annotations then [annotations] is empty
 */
data class SubTypesToAnnotations(
    val subTypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

typealias ClassOrObjectInvokes = MutableSet<SubTypesToAnnotations>
typealias FunctionInvokes = MutableSet<Set<String>>

data class ReflektInvokes(
    val objects: ClassOrObjectInvokes = HashSet(),
    val classes: ClassOrObjectInvokes = HashSet(),
    val functions: FunctionInvokes = HashSet()
) {
    companion object{
        // TODO: should I handle errors?
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ObjectInvokesProcessor }.first().invokes,
            classes = processors.mapNotNull { it as? ClassInvokesProcessor }.first().invokes,
            functions = processors.mapNotNull { it as? FunctionInvokesProcessor }.first().invokes
        )
    }
}

// We store the following structure:
// The map, where key is a set of annotationsFqNames, and value is
// a map, where key is a set of subTypesFqNames, and value is a list of fqNames
typealias ClassOrObjectUses = MutableMap<Set<String>, Map<Set<String>, List<String>>>
typealias FunctionUses = MutableMap<Set<String>, List<String>>

/*
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
// Todo: rename
data class ReflektUses(
    val objects: ClassOrObjectUses = HashMap(),
    val classes: ClassOrObjectUses = HashMap(),
    val functions: FunctionUses = HashMap()
) {
    companion object{
        // TODO: should I handle errors?
        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().uses,
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().uses,
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().uses
        )
    }
}
