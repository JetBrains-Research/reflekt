package io.reflekt.plugin.analysis.analyzer.source

import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.models.ReflektUses
import io.reflekt.plugin.analysis.processor.source.invokes.*
import io.reflekt.plugin.analysis.processor.source.uses.*
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(override val ktFiles: Set<KtFile>, override val binding: BindingContext, override val messageCollector: MessageCollector? = null) :
    BaseAnalyzer(ktFiles, binding, messageCollector) {
    fun uses(invokes: ReflektInvokes): ReflektUses {
        messageCollector?.log("Getting uses from sources....")
        val processors = setOf(
            ClassUsesProcessor(binding, invokes, messageCollector),
            ObjectUsesProcessor(binding, invokes, messageCollector),
            FunctionUsesProcessor(binding, invokes, messageCollector)
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
            ClassInvokesProcessor(binding, messageCollector),
            ObjectInvokesProcessor(binding, messageCollector),
            FunctionInvokesProcessor(binding, messageCollector)
        )
        ktFiles.forEach { file ->
            file.process(processors)
        }
        messageCollector?.log("Getting invokes from sources has done!")
        return ReflektInvokes.createByProcessors(processors)
    }
}
