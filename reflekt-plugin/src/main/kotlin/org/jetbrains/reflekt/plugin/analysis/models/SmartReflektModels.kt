package org.jetbrains.reflekt.plugin.analysis.models

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.source.instances.*
import org.jetbrains.reflekt.plugin.analysis.psi.function.toFunctionInfo
import org.jetbrains.reflekt.plugin.utils.Util.log

/*
 * Store a set of qualified names that exist in the project and additional libraries
 */
data class ReflektInstances(
    val objects: HashMap<FileID, MutableList<KtObjectDeclaration>> = HashMap(),
    val classes: HashMap<FileID, MutableList<KtClass>> = HashMap(),
    val functions: HashMap<FileID, MutableList<KtNamedFunction>> = HashMap()
) {
    companion object {
        fun createByProcessors(processors: Set<BaseInstancesProcessor<*>>) = ReflektInstances(
            objects = processors.mapNotNull { it as? ObjectInstancesProcessor }.first().fileToInstances,
            classes = processors.mapNotNull { it as? ClassInstancesProcessor }.first().fileToInstances,
            functions = processors.mapNotNull { it as? FunctionInstancesProcessor }.first().fileToInstances
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
        fun fromReflektInstances(instances: ReflektInstances, binding: BindingContext, messageCollector: MessageCollector? = null) = IrReflektInstances(
            objects = instances.objects.values.flatten().map { IrObjectInstance(it, it.fqName.toString()) },
            classes = instances.classes.values.flatten().map { IrClassInstance(it, it.fqName.toString()) },
            functions = instances.functions.values.flatten().map {
                messageCollector?.log(it.text)
                IrFunctionInstance(it, it.toFunctionInfo(binding))
            },
        )
    }
}

data class SupertypesToFilters(
    val supertype: KotlinType? = null,
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

data class TypeArgumentToFilters(
    val typeArgument: KotlinType? = null,
    val typeArgumentFqName: String? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList()
)

