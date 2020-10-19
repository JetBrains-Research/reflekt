package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.analysis.processor.Processor
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseUsesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    abstract val uses: Output

    protected fun processClassOrObjectUses(element: KtElement, invokes: ClassOrObjectInvokes, uses: ClassOrObjectUses): ClassOrObjectUses {
        (element as? KtClassOrObject)?.let {
            invokes.forEach {
                if (element.isSubtypeOf(it.subTypes, binding) && (it.annotations.isEmpty() || element.getAnnotations(binding, it.annotations).isNotEmpty())) {
                    uses.getOrPut(it.annotations, { HashMap() }).getOrPut(it.subTypes, { ArrayList() }).add(element.fqName!!.asString())
                }
            }
        }
        return uses
    }
}
