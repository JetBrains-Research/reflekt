package io.reflekt.plugin.analysis.analyzer

import io.reflekt.plugin.analysis.models.ReflektInstances
import io.reflekt.plugin.analysis.processor.instances.ClassInstancesProcessor
import io.reflekt.plugin.analysis.processor.instances.FunctionInstancesProcessor
import io.reflekt.plugin.analysis.processor.instances.ObjectInstancesProcessor
import io.reflekt.plugin.analysis.psi.visit

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class SmartReflektAnalyzer(override val ktFiles: Set<KtFile>, override val binding: BindingContext) : BaseAnalyzer(ktFiles, binding) {
    fun instances(): ReflektInstances {
        val processors = setOf(ClassInstancesProcessor(binding), ObjectInstancesProcessor(binding), FunctionInstancesProcessor(binding))
        ktFiles.forEach { file ->
            file.visit(processors)
        }
        return ReflektInstances.createByProcessors(processors)
    }
}
