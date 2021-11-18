package org.jetbrains.reflekt.plugin.analysis.analyzer.source

import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektInstances
import org.jetbrains.reflekt.plugin.analysis.processor.source.instances.*
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class SmartReflektAnalyzer(
    override val ktFiles: Set<KtFile>,
    override val binding: BindingContext,
    override val messageCollector: MessageCollector? = null) :
    BaseAnalyzer(
    ktFiles,
    binding,
    messageCollector) {
    fun instances(): ReflektInstances {
        messageCollector?.log("Getting instances....")
        val processors = setOf(ClassInstancesProcessor(binding), ObjectInstancesProcessor(binding), FunctionInstancesProcessor(binding))
        ktFiles.forEach { file ->
            file.process(processors)
        }
        messageCollector?.log("Getting instances has done!")
        return ReflektInstances.createByProcessors(processors)
    }
}
