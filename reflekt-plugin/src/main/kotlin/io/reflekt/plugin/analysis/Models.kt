package io.reflekt.plugin.analysis

import io.reflekt.Reflekt
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration

enum class ElementType(val value: String) {
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
    CALL_EXPRESSION("CALL_EXPRESSION")
}


/*
 * If the function [withAnnotations] is called without subtypes then [subTypes] is [setOf(Any::class::qualifiedName)]
 * If the function [withSubTypes] is called without annotations then [annotations] is empty
 */
data class SubTypesToAnnotations(
    val subTypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

data class ReflektInvokes(
    val objects: MutableSet<SubTypesToAnnotations> = HashSet(),
    val classes: MutableSet<SubTypesToAnnotations> = HashSet(),
    val functions: MutableSet<Set<String>> = HashSet()
)

/*
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
// Todo: rename
data class ReflektUses(
    val objects: MutableMap<SubTypesToAnnotations, Set<String>> = mutableMapOf(),
    val classes: MutableMap<SubTypesToAnnotations, Set<String>> = mutableMapOf(),
    val functions: MutableMap<Set<String>, String> = mutableMapOf()
)
