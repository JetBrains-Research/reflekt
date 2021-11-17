package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.kotlin.psi.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.source.uses.*

typealias TypeUses<K, V> = HashMap<K, MutableList<V>>
typealias ClassOrObjectUses = TypeUses<SupertypesToAnnotations, KtClassOrObject>
typealias FunctionUses = TypeUses<SignatureToAnnotations, KtNamedFunction>

/**
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
data class ReflektUses(
    override val objects: HashMap<FileID, ClassOrObjectUses> = HashMap(),
    override val classes: HashMap<FileID, ClassOrObjectUses> = HashMap(),
    override val functions: HashMap<FileID, FunctionUses> = HashMap()
) : BaseReflektDataByFile<ClassOrObjectUses, ClassOrObjectUses, FunctionUses>(objects, classes, functions) {
    companion object {
        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().fileToUses,
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().fileToUses,
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().fileToUses
        )
    }
}


