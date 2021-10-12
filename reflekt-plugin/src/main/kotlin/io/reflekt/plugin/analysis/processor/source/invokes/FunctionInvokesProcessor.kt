package io.reflekt.plugin.analysis.processor.source.invokes

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.common.findReflektFunctionInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.models.FunctionInvokes
import io.reflekt.plugin.analysis.processor.FileID
import io.reflekt.plugin.analysis.processor.fullName
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull

class FunctionInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseInvokesProcessor<FunctionInvokes>(binding, messageCollector) {
    override val fileToInvokes: HashMap<FileID, FunctionInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, FunctionInvokes> {
        (element as? KtReferenceExpression)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addIfNotNull(findReflektFunctionInvokeArgumentsByExpressionPart(element, binding))
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.FUNCTIONS.fqName
}
