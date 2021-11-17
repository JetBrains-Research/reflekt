package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.kotlin.psi.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.source.instances.*

typealias TypeInstances<T> = MutableList<T>
typealias ObjectInstances = TypeInstances<KtObjectDeclaration>
typealias ClassInstances = TypeInstances<KtClass>
typealias FunctionInstances = TypeInstances<KtNamedFunction>

/*
 * Store a set of qualified names that exist in the project and additional libraries
 */
data class ReflektInstances(
    override val objects: HashMap<FileID, ObjectInstances> = HashMap(),
    override val classes: HashMap<FileID, ClassInstances> = HashMap(),
    override val functions: HashMap<FileID, FunctionInstances> = HashMap()
): BaseReflektDataByFile<ObjectInstances, ClassInstances, FunctionInstances>(objects, classes, functions) {
    companion object {
        fun createByProcessors(processors: Set<BaseInstancesProcessor<*>>) = ReflektInstances(
            objects = processors.mapNotNull { it as? ObjectInstancesProcessor }.first().fileToInstances,
            classes = processors.mapNotNull { it as? ClassInstancesProcessor }.first().fileToInstances,
            functions = processors.mapNotNull { it as? FunctionInstancesProcessor }.first().fileToInstances
        )
    }
}
