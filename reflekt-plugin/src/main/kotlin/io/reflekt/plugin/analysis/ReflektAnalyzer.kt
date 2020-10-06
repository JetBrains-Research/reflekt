package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.processor.InvokesProcessor
import io.reflekt.plugin.analysis.processor.UsesProcessor
import io.reflekt.plugin.analysis.psi.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    // TODO: rename
    fun uses(invokes: ReflektInvokes): ReflektUses {
        val processor = UsesProcessor(binding, invokes)
        ktFiles.forEach { file ->
            file.visit(processor)
        }
        return processor.reflektUses
    }

    fun invokes(): ReflektInvokes {
        val processor = InvokesProcessor(binding)
        ktFiles.forEach { file ->
            file.visit(processor)
        }
        return processor.invokes
    }
}
