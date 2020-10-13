package io.reflekt.plugin.analysis.processor

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.ReflektInvokes
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

class InvokesProcessor(override val binding: BindingContext): Processor<ReflektInvokes>(binding) {
    val invokes = ReflektInvokes()

    private enum class ReflektNames(val key: String){
        OBJECTS(Reflekt.Objects::class.qualifiedName!!),
        CLASSES(Reflekt.Classes::class.qualifiedName!!)
    }

    override fun process(element: KtElement): ReflektInvokes {
        TODO("Not yet implemented")
    }

    private fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) in ReflektNames.values().map { it.key }

    override fun shouldRunOn(element: KtElement) = (element as? KtReferenceExpression)?.let{ isValidExpression(it) } ?: false
}
