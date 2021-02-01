package io.reflekt.plugin.analysis.processor.instances

import io.reflekt.plugin.analysis.processor.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class FunctionInstancesProcessor(override val binding: BindingContext) : BaseInstancesProcessor<List<KtNamedFunction>>(binding) {
    override val instances: MutableList<KtNamedFunction> = ArrayList()

    override fun process(element: KtElement): List<KtNamedFunction> {
        (element as? KtNamedFunction)?.let {
            instances.add(it)
        }
        return instances
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicFunction
}
