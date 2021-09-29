package org.jetbrains.reflekt.plugin.analysis.analyzer

import org.jetbrains.reflekt.plugin.analysis.models.ReflektInvokes
import org.jetbrains.reflekt.plugin.analysis.models.ReflektUses
import org.jetbrains.reflekt.plugin.analysis.processor.invokes.*
import org.jetbrains.reflekt.plugin.analysis.processor.uses.*
import org.jetbrains.reflekt.plugin.analysis.psi.visit
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(override val ktFiles: Set<KtFile>, override val binding: BindingContext) : BaseAnalyzer(ktFiles, binding) {
    fun uses(invokes: ReflektInvokes): ReflektUses {
        val processors = setOf(ClassUsesProcessor(binding, invokes), ObjectUsesProcessor(binding, invokes), FunctionUsesProcessor(binding, invokes))
        ktFiles.forEach { file ->
            file.visit(processors)
        }
        return ReflektUses.createByProcessors(processors)
    }

    fun invokes(): ReflektInvokes {
        val processors = setOf(ClassInvokesProcessor(binding), ObjectInvokesProcessor(binding), FunctionInvokesProcessor(binding))
        ktFiles.forEach { file ->
            file.visit(processors)
        }
        return ReflektInvokes.createByProcessors(processors)
    }
}
