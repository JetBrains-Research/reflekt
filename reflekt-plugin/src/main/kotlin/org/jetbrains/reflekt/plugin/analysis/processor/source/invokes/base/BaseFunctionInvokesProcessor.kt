package org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.base

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull
import org.jetbrains.reflekt.plugin.analysis.common.findReflektFunctionInvokeArgumentsByExpressionPart
import org.jetbrains.reflekt.plugin.analysis.models.FunctionInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.fullName

abstract class BaseFunctionInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseInvokesProcessor<FunctionInvokes>(binding, messageCollector) {
    override val fileToInvokes: HashMap<FileID, FunctionInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, FunctionInvokes> {
        (element as? KtReferenceExpression)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addIfNotNull(findReflektFunctionInvokeArgumentsByExpressionPart(element, binding))
        }
        return fileToInvokes
    }
}
