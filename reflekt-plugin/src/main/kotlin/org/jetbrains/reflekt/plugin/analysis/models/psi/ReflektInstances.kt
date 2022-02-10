package org.jetbrains.reflekt.plugin.analysis.models.psi

import org.jetbrains.reflekt.plugin.analysis.models.BaseReflektDataByFile
import org.jetbrains.reflekt.plugin.analysis.processor.FileId

import org.jetbrains.kotlin.psi.*

typealias TypeInstances<T> = MutableList<T>
typealias ObjectInstances = TypeInstances<KtObjectDeclaration>
typealias ClassInstances = TypeInstances<KtClass>
typealias FunctionInstances = TypeInstances<KtNamedFunction>

/**
 * @property objects
 * @property classes
 * @property functions
 */
@Suppress("COMPLEX_EXPRESSION")
data class ReflektInstances(
    override val objects: HashMap<FileId, ObjectInstances> = HashMap(),
    override val classes: HashMap<FileId, ClassInstances> = HashMap(),
    override val functions: HashMap<FileId, FunctionInstances> = HashMap(),
) : BaseReflektDataByFile<ObjectInstances, ClassInstances, FunctionInstances>(
    objects,
    classes,
    functions)
