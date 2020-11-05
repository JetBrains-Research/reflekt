package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.common.findReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull

abstract class BaseInvokesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    abstract val invokes: Output

    protected fun processClassOrObjectInvokes(element: KtElement): ClassOrObjectInvokes {
        val invokes: ClassOrObjectInvokes = HashSet()
        (element as? KtReferenceExpression)?.let { expression ->
            invokes.addIfNotNull(findReflektInvokeArgumentsByExpressionPart(expression, binding))
        }
        return invokes
    }

    protected abstract fun isValidExpression(expression: KtReferenceExpression): Boolean

    override fun shouldRunOn(element: KtElement) = (element as? KtReferenceExpression)?.let { isValidExpression(it) } ?: false
}
