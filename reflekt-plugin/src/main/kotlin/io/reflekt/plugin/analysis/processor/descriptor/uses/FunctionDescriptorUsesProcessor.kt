package io.reflekt.plugin.analysis.processor.descriptor.uses

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.*
import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import io.reflekt.plugin.analysis.resolve.toFunctionInfo
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
        (descriptor as? FunctionDescriptor)?.let {
            invokes.filter { it.covers(descriptor) }.forEach {
                uses.getOrPut(it) { mutableListOf() }.add(descriptor.toFunctionInfo())
            }
        }
        return uses
    }

    override fun shouldRunOn(descriptor: DeclarationDescriptor): Boolean = descriptor.isPublicTopLevelFunction && !descriptor.isMainFunction

    private fun SignatureToAnnotations.covers(function: FunctionDescriptor): Boolean =
        shouldCheckAnnotations(annotations, function) && function.toParameterizedType()?.isSubtypeOf(signature!!) ?: false

}
