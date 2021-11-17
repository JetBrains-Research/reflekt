package io.reflekt.plugin.analysis.analyzer.descriptor

import io.reflekt.plugin.analysis.models.IrReflektUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.processor.descriptor.uses.*
import io.reflekt.plugin.analysis.resolve.*
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.resolve.scopes.MemberScope

class DescriptorAnalyzer(memberScope: MemberScope, private val messageCollector: MessageCollector? = null) {
    private val classDescriptors = memberScope.getClassDescriptors()
    private val objectDescriptors = memberScope.getObjectDescriptors()
    private val topLevelFunctionsDescriptors = memberScope.getTopLevelFunctionDescriptors()

    private fun <Output : Any> BaseDescriptorUsesProcessor<Output>.runProcessor(descriptors: List<DeclarationDescriptor>): Output {
        descriptors.forEach { this.process(it) }
        return this.uses
    }

    fun uses(invokes: ReflektInvokes): IrReflektUses {
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
