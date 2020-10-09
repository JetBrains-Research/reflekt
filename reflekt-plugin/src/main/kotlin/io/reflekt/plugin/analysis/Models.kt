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
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
    CALL_EXPRESSION("CALL_EXPRESSION"),
    DOT_QUALIFIED_EXPRESSION("DOT_QUALIFIED_EXPRESSION"),
    VALUE_ARGUMENT_LIST("VALUE_ARGUMENT_LIST"),
    VALUE_ARGUMENT("VALUE_ARGUMENT")
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

typealias ClassOrObjectUses = MutableMap<SubTypesToAnnotations, Set<String>>
typealias FunctionUses = MutableMap<SubTypesToAnnotations, Set<String>>

/*
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
// Todo: rename
data class ReflektUses(
    val objects: ClassOrObjectUses = mutableMapOf(),
    val classes: ClassOrObjectUses = mutableMapOf(),
    val functions: FunctionUses = mutableMapOf()
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
