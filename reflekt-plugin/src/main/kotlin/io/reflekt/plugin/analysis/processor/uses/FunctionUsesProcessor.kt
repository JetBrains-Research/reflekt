package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.isPublicFunction
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf


class FunctionUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<FunctionUses>(binding) {
    override val uses: FunctionUses = reflektInvokes.functions.associateWith { ArrayList() }

    override fun process(element: KtElement): FunctionUses {
        (element as? KtNamedFunction)?.let {
            reflektInvokes.functions.forEach {
                if (it.covers(element)) {
                    uses.getValue(it).add(element)
                }
            }
        }
        return uses
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicFunction

    private fun SignatureToAnnotations.covers(function: KtNamedFunction): Boolean {
        return (annotations.isEmpty() || function.getAnnotations(binding, annotations).isNotEmpty()) &&
            function.toParameterizedType(binding)?.isSubtypeOf(signature) ?: false
    }
}
