package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.processor.invokes.ClassInvokesProcessor
import io.reflekt.plugin.analysis.processor.invokes.FunctionInvokesProcessor
import io.reflekt.plugin.analysis.processor.invokes.ObjectInvokesProcessor
import io.reflekt.plugin.analysis.processor.uses.ClassUsesProcessor
import io.reflekt.plugin.analysis.processor.uses.FunctionUsesProcessor
import io.reflekt.plugin.analysis.processor.uses.ObjectUsesProcessor
import io.reflekt.plugin.analysis.psi.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    // TODO: rename
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
            println(file.name)
            file.visit(processors)
        }
        return ReflektInvokes.createByProcessors(processors)
    }
}
