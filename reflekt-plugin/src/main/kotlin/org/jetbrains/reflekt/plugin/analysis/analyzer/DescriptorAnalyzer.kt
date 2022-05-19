package org.jetbrains.reflekt.plugin.analysis.analyzer

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrReflektUses
import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.descriptor.uses.BaseDescriptorUsesProcessor
import org.jetbrains.reflekt.plugin.analysis.processor.descriptor.uses.ClassDescriptorUsesProcessor
import org.jetbrains.reflekt.plugin.analysis.processor.descriptor.uses.FunctionDescriptorUsesProcessor
import org.jetbrains.reflekt.plugin.analysis.processor.descriptor.uses.ObjectDescriptorUsesProcessor
import org.jetbrains.reflekt.plugin.analysis.resolve.getClassDescriptors
import org.jetbrains.reflekt.plugin.analysis.resolve.getObjectDescriptors
import org.jetbrains.reflekt.plugin.analysis.resolve.getTopLevelFunctionDescriptors
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.scopes.MemberScope

class DescriptorAnalyzer(memberScope: MemberScope, private val messageCollector: MessageCollector? = null) {
    private val classDescriptors = memberScope.getClassDescriptors()
    private val objectDescriptors = memberScope.getObjectDescriptors()
    private val topLevelFunctionsDescriptors = memberScope.getTopLevelFunctionDescriptors()

    private fun <T : Any> BaseDescriptorUsesProcessor<T>.runProcessor(descriptors: List<DeclarationDescriptor>): T {
        descriptors.forEach { this.process(it) }
        return this.uses
    }

    fun uses(invokes: ReflektInvokes): IrReflektUses {
        // Try to find uses only if some Reflekt calls were found
        if (invokes.isEmpty()) {
            messageCollector?.log("Got empty invokes")
            return IrReflektUses()
        }
        messageCollector?.log("Getting uses from descriptors....")
        val uses = IrReflektUses(
            classes = ClassDescriptorUsesProcessor(invokes, messageCollector).runProcessor(classDescriptors),
            objects = ObjectDescriptorUsesProcessor(invokes, messageCollector).runProcessor(objectDescriptors),
            functions = FunctionDescriptorUsesProcessor(invokes, messageCollector).runProcessor(topLevelFunctionsDescriptors),
        )
        messageCollector?.log("Getting uses from descriptors has done!")
        return uses
    }
}
