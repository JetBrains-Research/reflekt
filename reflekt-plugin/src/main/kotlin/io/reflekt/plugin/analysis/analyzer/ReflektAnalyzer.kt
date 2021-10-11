package io.reflekt.plugin.analysis.analyzer

import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.models.ReflektUses
import io.reflekt.plugin.analysis.processor.invokes.ClassInvokesProcessor
import io.reflekt.plugin.analysis.processor.invokes.FunctionInvokesProcessor
import io.reflekt.plugin.analysis.processor.invokes.ObjectInvokesProcessor
import io.reflekt.plugin.analysis.processor.uses.ClassUsesProcessor
import io.reflekt.plugin.analysis.processor.uses.FunctionUsesProcessor
import io.reflekt.plugin.analysis.processor.uses.ObjectUsesProcessor
import io.reflekt.plugin.analysis.psi.visit
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(override val ktFiles: Set<KtFile>, override val binding: BindingContext, override val messageCollector: MessageCollector? = null) :
    BaseAnalyzer(ktFiles, binding, messageCollector) {
    fun uses(invokes: ReflektInvokes): ReflektUses {
        messageCollector?.log("Getting uses....")
        val processors = setOf(ClassUsesProcessor(binding, invokes), ObjectUsesProcessor(binding, invokes), FunctionUsesProcessor(binding, invokes))
        ktFiles.forEach { file ->
            file.process(processors)
        }
        messageCollector?.log("Getting uses has done!")
        return ReflektUses.createByProcessors(processors)
    }

    fun invokes(): ReflektInvokes {
        messageCollector?.log("Getting invokes....")
        val processors = setOf(ClassInvokesProcessor(binding), ObjectInvokesProcessor(binding), FunctionInvokesProcessor(binding))
        ktFiles.forEach { file ->
            file.process(processors)
        }
        messageCollector?.log("Getting invokes has done!")
        return ReflektInvokes.createByProcessors(processors)
    }
}
