package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.FunctionUses
import io.reflekt.plugin.analysis.ReflektInvokes
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class FunctionUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<FunctionUses>(binding) {
    override val uses: FunctionUses = mutableMapOf()

    override fun process(element: KtElement): FunctionUses {
        TODO("Not yet implemented")
    }

    private fun isValidFunction(function: KtFunction) = reflektInvokes.functions.map { function.getAnnotations(binding, it) }.any { it.isNotEmpty() }

    override fun shouldRunOn(element: KtElement) = (element as? KtFunction)?.let{ isValidFunction(it) } ?: false
}
