package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.common.findReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.Processor

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull

abstract class BaseInvokesProcessor<T : Any>(override val binding: BindingContext) : Processor<T>(binding) {
    // Store invokes by file
    abstract val fileToInvokes: HashMap<FileId, T>

    protected fun processClassOrObjectInvokes(element: KtElement): ClassOrObjectInvokes? {
        val invokes: ClassOrObjectInvokes = HashSet()
        (element as? KtReferenceExpression)?.let { expression ->
            invokes.addIfNotNull(findReflektInvokeArgumentsByExpressionPart(expression, binding))
        }
        return invokes.ifEmpty { null }
    }

    protected abstract fun isValidExpression(expression: KtReferenceExpression): Boolean

    override fun shouldRunOn(element: KtElement) = (element as? KtReferenceExpression)?.let { isValidExpression(it) } ?: false
}
