package io.reflekt.plugin.analysis.analyzer

import io.reflekt.plugin.analysis.*
import io.reflekt.plugin.analysis.models.ReflektInstances
import io.reflekt.plugin.analysis.processor.instances.*
import io.reflekt.plugin.analysis.psi.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class SmartReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    fun instances(): ReflektInstances {
        val processors = setOf(ClassInstancesProcessor(binding), ObjectInstancesProcessor(binding), FunctionInstancesProcessor(binding))
        ktFiles.forEach { file ->
            file.visit(processors)
        }
        return ReflektInstances.createByProcessors(processors)
    }
}
