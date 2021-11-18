package org.jetbrains.reflekt.plugin.analysis.analyzer.source

import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektInvokes
import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektUses
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.reflekt.*
import org.jetbrains.reflekt.plugin.analysis.processor.source.uses.*
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(
    override val ktFiles: Set<KtFile>,
    override val binding: BindingContext,
    override val messageCollector: MessageCollector? = null) :
    BaseAnalyzer(
    ktFiles,
    binding,
    messageCollector) {
    fun uses(invokes: ReflektInvokes): ReflektUses {
        // Try to find uses only if some Reflekt calls were found
        if (invokes.isEmpty()) {
            messageCollector?.log("Got empty invokes")
            return ReflektUses()
        }
        messageCollector?.log("Getting uses from sources....")
        val processors = setOf(
            ClassUsesProcessor(binding, invokes, messageCollector),
            ObjectUsesProcessor(binding, invokes, messageCollector),
            FunctionUsesProcessor(binding, invokes, messageCollector),
        )
        ktFiles.forEach { file ->
            file.process(processors)
        }
        messageCollector?.log("Getting uses from sources has done!")
        return ReflektUses.createByProcessors(processors)
    }

    fun invokes(): ReflektInvokes {
        messageCollector?.log("Getting invokes from sources....")
        val processors = setOf(
            ReflektClassInvokesProcessor(binding, messageCollector),
            ReflektObjectInvokesProcessor(binding, messageCollector),
            ReflektFunctionInvokesProcessor(binding, messageCollector),
        )
        ktFiles.forEach { file ->
            file.process(processors)
        }
        messageCollector?.log("Getting invokes from sources has done!")
        return ReflektInvokes.createByProcessors(processors)
    }
}
