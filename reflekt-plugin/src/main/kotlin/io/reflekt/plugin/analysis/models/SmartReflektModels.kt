package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.instances.*
import io.reflekt.plugin.analysis.psi.function.toFunctionInfo
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

/*
 * Store a set of qualified names that exist in the project and additional libraries
 */
data class ReflektInstances(
    val objects: List<KtObjectDeclaration> = ArrayList(),
    val classes: List<KtClass> = ArrayList(),
    val functions: List<KtNamedFunction> = ArrayList()
) {
    companion object{
        fun createByProcessors(processors: Set<BaseInstancesProcessor<*>>) = ReflektInstances(
            objects = processors.mapNotNull { it as? ObjectInstancesProcessor }.first().instances,
            classes = processors.mapNotNull { it as? ClassInstancesProcessor }.first().instances,
            functions = processors.mapNotNull { it as? FunctionInstancesProcessor }.first().instances
        )
    }
}

data class IrTypeInstance<T, I>(
    val instance: T,
    val info: I
)

typealias IrObjectInstance = IrTypeInstance<KtObjectDeclaration, String>
typealias IrClassInstance = IrTypeInstance<KtClass, String>
typealias IrFunctionInstance = IrTypeInstance<KtNamedFunction, IrFunctionInfo>

data class IrReflektInstances(
    val objects: List<IrObjectInstance> = ArrayList(),
    val classes: List<IrClassInstance> = ArrayList(),
    val functions: List<IrFunctionInstance> = ArrayList()
) {
    companion object {
        fun fromReflektInstances(instances: ReflektInstances, binding: BindingContext) = IrReflektInstances(
            objects = instances.objects.map { IrObjectInstance(it, it.fqName.toString()) },
            classes = instances.classes.map { IrClassInstance(it, it.fqName.toString()) },
            functions = instances.functions.map { IrFunctionInstance(it, it.toFunctionInfo(binding)) },
        )
    }
}

data class SubTypesToFilters(
    val subType: ParameterizedType? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList()
)

data class Lambda(
    val body: String,
    val parameters: List<String> = listOf("it")
)

data class Import(
    val fqName: String,
    val text: String
)

data class SourceFile(
    val imports: List<Import>,
    val content: String
)
