package org.jetbrains.reflekt.plugin.analysis.models.ir

import org.jetbrains.reflekt.plugin.analysis.models.BaseCollectionReflektData
import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektInstances
import org.jetbrains.reflekt.plugin.analysis.psi.function.toFunctionInfo
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

typealias IrObjectInstance = IrTypeInstance<KtObjectDeclaration, String>
typealias IrClassInstance = IrTypeInstance<KtClass, String>
typealias IrFunctionInstance = IrTypeInstance<KtNamedFunction, IrFunctionInfo>

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
@Suppress("COMPLEX_EXPRESSION")
data class IrReflektInstances(
    override val objects: List<IrObjectInstance> = ArrayList(),
    override val classes: List<IrClassInstance> = ArrayList(),
    override val functions: List<IrFunctionInstance> = ArrayList(),
) : BaseCollectionReflektData<List<IrObjectInstance>, List<IrClassInstance>, List<IrFunctionInstance>>(
    objects,
    classes,
    functions) {
    companion object {
        fun fromReflektInstances(
            instances: ReflektInstances,
            binding: BindingContext,
            messageCollector: MessageCollector? = null) = IrReflektInstances(
            objects = instances.objects.values.flatten().map { IrObjectInstance(it, it.fqName.toString()) },
            classes = instances.classes.values.flatten().map { IrClassInstance(it, it.fqName.toString()) },
            functions = instances.functions.values.flatten().map {
                messageCollector?.log(it.text)
                IrFunctionInstance(it, it.toFunctionInfo(binding))
            },
        )
    }
}
