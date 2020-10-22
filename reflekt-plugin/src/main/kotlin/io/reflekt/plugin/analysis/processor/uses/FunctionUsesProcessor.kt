package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.FunctionUses
import io.reflekt.plugin.analysis.ReflektInvokes
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.resolve.BindingContext

class FunctionUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<FunctionUses>(binding) {
    override val uses: FunctionUses = HashMap()

    override fun process(element: KtElement): FunctionUses {
        (element as? KtNamedFunction)?.let {
            reflektInvokes.functions.forEach {
                if (element.getAnnotations(binding, it).isNotEmpty()) {
                    uses.getOrPut(it, { ArrayList() }).add(element)
                }
            }
        }
        return uses
    }

    override fun shouldRunOn(element: KtElement) = element is KtNamedFunction && element.isPublic
}
