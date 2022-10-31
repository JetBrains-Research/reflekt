@file:Suppress("PACKAGE_NAME_INCORRECT_CASE")

package org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments

import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.ir.ReflektInvokeArgumentsCollector
import org.jetbrains.reflekt.plugin.analysis.ir.isSubtypeOf
import org.jetbrains.reflekt.plugin.analysis.models.SupertypesToAnnotations

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.name.FqName

/**
 * A base class to process objects and classes Reflekt queries and extract the arguments.
 */
abstract class IrClassOrObjectArgumentProcessor(private val irInstances: List<IrClass>, private val context: IrPluginContext) :
    IrReflektArgumentProcessor<SupertypesToAnnotations, IrClass>() {
    override fun IrCall.collectQueryArguments() = ReflektInvokeArgumentsCollector.collectInvokeArguments(this)

    override fun filterInstances(queryArguments: SupertypesToAnnotations) =
        irInstances.filter { it.hasAnnotationFrom(queryArguments.annotations) && it.isSubtypeOfAny(queryArguments.supertypes) }.toSet()

    /**
     * Check if the [IrClass] is subtype of at least one type from [superTypes].
     *
     * @param superTypes set of possible supertypes for [IrClass].
     */
    private fun IrClass.isSubtypeOfAny(superTypes: Set<String>): Boolean {
        require(superTypes.isNotEmpty()) { "The set of super types is empty" }
        return superTypes.mapNotNull { context.referenceClass(FqName(it)) }.any { this.isSubtypeOf(it.defaultType, context) }
    }
}

/**
 * Processes classes Reflekt queries and extract the arguments.
 */
class IrClassArgumentProcessor(irInstances: List<IrClass>, context: IrPluginContext) : IrClassOrObjectArgumentProcessor(irInstances, context) {
    override val reflektEntity = ReflektEntity.CLASSES
}

/**
 * Processes objects Reflekt queries and extract the arguments.
 */
class IrObjectArgumentProcessor(irInstances: List<IrClass>, context: IrPluginContext) : IrClassOrObjectArgumentProcessor(irInstances, context) {
    override val reflektEntity = ReflektEntity.OBJECTS
}
