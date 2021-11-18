package org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.reflekt

import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.base.BaseFunctionInvokesProcessor
import org.jetbrains.reflekt.plugin.analysis.psi.getFqName

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektFunctionInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseFunctionInvokesProcessor(binding, messageCollector) {
    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.FUNCTIONS.fqName
}
