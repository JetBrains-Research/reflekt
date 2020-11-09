package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.FunctionInvokes
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

class FunctionInvokesProcessor (override val binding: BindingContext): BaseInvokesProcessor<FunctionInvokes>(binding){
    override val invokes: FunctionInvokes = HashSet()

    override fun process(element: KtElement): FunctionInvokes {
        invokes.addAll(processClassOrObjectInvokes(element, false).map { it.annotations })
        return invokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektFqNames.FUNCTIONS.fqName
}
