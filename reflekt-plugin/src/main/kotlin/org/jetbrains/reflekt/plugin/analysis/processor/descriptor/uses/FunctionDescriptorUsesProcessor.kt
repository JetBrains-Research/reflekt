package org.jetbrains.reflekt.plugin.analysis.processor.descriptor.uses

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrFunctionUses
import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektInvokes
import org.jetbrains.reflekt.plugin.analysis.models.psi.SignatureToAnnotations
import org.jetbrains.reflekt.plugin.analysis.processor.*
import org.jetbrains.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.reflekt.plugin.analysis.resolve.toFunctionInfo

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

/**
 * @param reflektInvokes
 *
 * @property messageCollector
 */
class FunctionDescriptorUsesProcessor(reflektInvokes: ReflektInvokes, override val messageCollector: MessageCollector?) :
    BaseDescriptorUsesProcessor<IrFunctionUses>(messageCollector) {
    override val uses: IrFunctionUses = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.functions)

    override fun process(descriptor: DeclarationDescriptor): IrFunctionUses {
        if (descriptor is FunctionDescriptor) {
            invokes.filter { it.isCovering(descriptor) }
                .forEach { signatureToAnnotations ->
                    uses.getOrPut(signatureToAnnotations) { mutableListOf() }.add(descriptor.toFunctionInfo())
                }
        }
        return uses
    }

    // TODO: how can we return the member functions??
    override fun shouldRunOn(descriptor: DeclarationDescriptor): Boolean = descriptor.isPublicTopLevelFunction && !descriptor.isMainFunction

    private fun SignatureToAnnotations.isCovering(function: FunctionDescriptor): Boolean =
        shouldCheckAnnotations(annotations, function) && function.toParameterizedType()?.isSubtypeOf(signature!!) ?: false
}
