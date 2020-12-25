package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.ReflektUses
import io.reflekt.plugin.analysis.models.SubTypesToAnnotations
import io.reflekt.plugin.analysis.models.TypeUses
import org.jetbrains.kotlin.psi.KtNamedDeclaration

typealias TypeUsesTest<K> = Map<K, Set<String>>
typealias ClassOrObjectUsesTest = TypeUsesTest<SubTypesToAnnotations>
typealias FunctionUsesTest = TypeUsesTest<Set<String>>

data class ReflektUsesTest(
    val objects: ClassOrObjectUsesTest = HashMap(),
    val classes: ClassOrObjectUsesTest = HashMap(),
    val functions: FunctionUsesTest = HashMap()
)

private fun <K, V: KtNamedDeclaration> fromTypeUses(uses: TypeUses<K, V>) : TypeUsesTest<K> {
    return uses.mapValues { (_, items) ->
        items.map { it.fqName!!.toString() }.toSet()
    }
}

fun ReflektUses.toTestUses() = ReflektUsesTest(
    objects = fromTypeUses(objects),
    classes = fromTypeUses(classes),
    functions = fromTypeUses(functions)
)
