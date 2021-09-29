package org.jetbrains.reflekt.plugin.analysis.processor.invokes

import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.common.findReflektFunctionInvokeArgumentsByExpressionPart
import org.jetbrains.reflekt.plugin.analysis.models.FunctionInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.fullName
import org.jetbrains.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull

// TODO: should we filter functions with <main> name? since it can be error-prone
class FunctionInvokesProcessor(override val binding: BindingContext) : BaseInvokesProcessor<FunctionInvokes>(binding) {
    override val fileToInvokes: HashMap<FileID, FunctionInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, FunctionInvokes> {
        (element as? KtReferenceExpression)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addIfNotNull(findReflektFunctionInvokeArgumentsByExpressionPart(element, binding))
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.FUNCTIONS.fqName
}
