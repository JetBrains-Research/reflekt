@file:Suppress("PACKAGE_NAME_INCORRECT_CASE")

package org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments

import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.ir.ReflektFunctionInvokeArgumentsCollector
import org.jetbrains.reflekt.plugin.analysis.ir.isSubtypeOf
import org.jetbrains.reflekt.plugin.analysis.models.psi.SignatureToAnnotations

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Processes functions Reflekt queries and extract the arguments.
 */
class IrFunctionArgumentProcessor(private val irInstances: List<IrFunction>, private val context: IrPluginContext) :
    IrReflektArgumentProcessor<SignatureToAnnotations, IrFunction>() {
    override val reflektEntity = ReflektEntity.FUNCTIONS

    override fun IrCall.collectQueryArguments() = ReflektFunctionInvokeArgumentsCollector.collectInvokeArguments(this)

    override fun filterInstances(queryArguments: SignatureToAnnotations) =
        irInstances.filter { it.hasAnnotationFrom(queryArguments.annotations) && it.isSubtypeOf(queryArguments.irSignature) }.toSet()

    private fun IrFunction.isSubtypeOf(supertype: IrType?) = supertype?.let {
        this.isSubtypeOf(supertype, context)
    } ?: false
}
