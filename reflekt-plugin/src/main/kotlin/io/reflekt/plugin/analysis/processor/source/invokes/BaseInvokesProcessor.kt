package io.reflekt.plugin.analysis.processor.source.invokes

import io.reflekt.plugin.analysis.common.findReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.source.Processor
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull

/**
 * @property binding
 * @property messageCollector
 */
abstract class BaseInvokesProcessor<T : Any>(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    Processor<T>(binding, messageCollector) {
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

    private fun isValidExpressionWithLog(expression: KtReferenceExpression): Boolean {
        val isValid = isValidExpression(expression)
        messageCollector?.log("Expression ${expression.text} is valid: $isValid")
        return isValid
    }

    override fun shouldRunOn(element: KtElement) = (element as? KtReferenceExpression)?.let { isValidExpressionWithLog(it) } ?: false
}
