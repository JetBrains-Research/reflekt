package org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.reflekt

import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.base.BaseObjectInvokesProcessor
import org.jetbrains.reflekt.plugin.analysis.psi.getFqName

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

class ReflektObjectInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseObjectInvokesProcessor(binding, messageCollector) {
    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.OBJECTS.fqName
}
