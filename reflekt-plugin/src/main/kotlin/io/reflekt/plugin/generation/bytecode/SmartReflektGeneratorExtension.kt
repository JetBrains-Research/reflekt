package io.reflekt.plugin.generation.bytecode

import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class SmartReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : BaseReflektGeneratorExtension() {

    override fun myApplyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        val binding = c.codegen.bindingContext
        val expression = resolvedCall.call.calleeExpression ?: return null

        val expressionFqName = expression.getFqName(c.codegen.bindingContext) ?: return null

        TODO("Get instances, Replace bytecode if it is necessary")
    }
}
