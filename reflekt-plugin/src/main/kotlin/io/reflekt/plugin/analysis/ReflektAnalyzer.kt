package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.processor.ClassProcessor
import io.reflekt.plugin.analysis.processor.FunctionProcessor
import io.reflekt.plugin.analysis.processor.InvokesProcessor
import io.reflekt.plugin.analysis.processor.ObjectProcessor
import io.reflekt.plugin.analysis.psi.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    // TODO: rename
    fun uses(invokes: ReflektInvokes): ReflektUses {
        val classProcessor = ClassProcessor(binding, invokes)
        val objectProcessor = ObjectProcessor(binding, invokes)
        val functionProcessor = FunctionProcessor(binding, invokes)
        val processors = setOf(classProcessor, objectProcessor, functionProcessor)
        ktFiles.forEach { file ->
            file.visit(processors)
        }
        return ReflektUses(
            classes = classProcessor.classes,
            objects = objectProcessor.objects,
            functions = functionProcessor.functions
        )
    }

    fun invokes(): ReflektInvokes {
        val processor = InvokesProcessor(binding)
        ktFiles.forEach { file ->
            file.visit(setOf(processor))
        }
        return processor.invokes
    }
}
