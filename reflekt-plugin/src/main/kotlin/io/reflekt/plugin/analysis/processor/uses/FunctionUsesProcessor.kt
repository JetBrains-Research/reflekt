package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.FunctionUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.processor.isPublicFunction
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

class FunctionUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<FunctionUses>(binding) {
    override val uses: FunctionUses = reflektInvokes.functions.map { it to ArrayList<KtNamedFunction>() }.toMap()

    override fun process(element: KtElement): FunctionUses {
        (element as? KtNamedFunction)?.let {
            reflektInvokes.functions.forEach {
                if (element.getAnnotations(binding, it).isNotEmpty()) {
                    uses.getValue(it).add(element)
                }
            }
        }
        return uses
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicFunction
}
