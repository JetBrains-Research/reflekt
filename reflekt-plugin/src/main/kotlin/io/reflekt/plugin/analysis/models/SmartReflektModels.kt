package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.instances.BaseInstancesProcessor
import io.reflekt.plugin.analysis.processor.instances.ClassInstancesProcessor
import io.reflekt.plugin.analysis.processor.instances.FunctionInstancesProcessor
import io.reflekt.plugin.analysis.processor.instances.ObjectInstancesProcessor
import io.reflekt.plugin.analysis.psi.function.toFunctionInfo

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

typealias IrObjectInstance = IrTypeInstance<KtObjectDeclaration, String>
typealias IrClassInstance = IrTypeInstance<KtClass, String>
typealias IrFunctionInstance = IrTypeInstance<KtNamedFunction, IrFunctionInfo>

/**
 * @property objects
 * @property classes
 * @property functions
 */
/*
 * Store a set of qualified names that exist in the project and additional libraries
 */
data class ReflektInstances(
    val objects: HashMap<FileId, MutableList<KtObjectDeclaration>> = HashMap(),
    val classes: HashMap<FileId, MutableList<KtClass>> = HashMap(),
    val functions: HashMap<FileId, MutableList<KtNamedFunction>> = HashMap(),
) {
    companion object {
        fun createByProcessors(processors: Set<BaseInstancesProcessor<*>>) = ReflektInstances(
            objects = processors.mapNotNull { it as? ObjectInstancesProcessor }.first().fileToInstances,
            classes = processors.mapNotNull { it as? ClassInstancesProcessor }.first().fileToInstances,
            functions = processors.mapNotNull { it as? FunctionInstancesProcessor }.first().fileToInstances,
        )
    }
}

/**
 * @property instance
 * @property info
 */
data class IrTypeInstance<T, I>(
    val instance: T,
    val info: I,
)

/**
 * @property objects
 * @property classes
 * @property functions
 */
data class IrReflektInstances(
    val objects: List<IrObjectInstance> = ArrayList(),
    val classes: List<IrClassInstance> = ArrayList(),
    val functions: List<IrFunctionInstance> = ArrayList(),
) {
    companion object {
        fun fromReflektInstances(instances: ReflektInstances, binding: BindingContext) = IrReflektInstances(
            objects = instances.objects.values.flatten().map { IrObjectInstance(it, it.fqName.toString()) },
            classes = instances.classes.values.flatten().map { IrClassInstance(it, it.fqName.toString()) },
            functions = instances.functions.values.flatten().map { IrFunctionInstance(it, it.toFunctionInfo(binding)) },
        )
    }
}

/**
 * @property supertype
 * @property filters
 * @property imports
 */
data class SupertypesToFilters(
    val supertype: KotlinType? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList(),
)

/**
 * @property body
 * @property parameters
 */
data class Lambda(
    val body: String,
    val parameters: List<String> = listOf("it"),
)

/**
 * @property fqName
 * @property text
 */
data class Import(
    val fqName: String,
    val text: String,
)

/**
 * @property imports
 * @property content
 */
data class SourceFile(
    val imports: List<Import>,
    val content: String,
)

/**
 * @property typeArgument
 * @property typeArgumentFqName
 * @property filters
 * @property imports
 */
data class TypeArgumentToFilters(
    val typeArgument: KotlinType? = null,
    val typeArgumentFqName: String? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList(),
)
