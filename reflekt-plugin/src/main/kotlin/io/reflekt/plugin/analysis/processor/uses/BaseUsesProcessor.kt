package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.analysis.SubTypesToAnnotations
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
                if (it.covers(element)) {
                    uses.getValue(it.annotations).getValue(it.subTypes).add(element.fqName!!.asString())
                }
            }
        }
        return uses
    }

    protected fun initClassOrObjectUses(invokes: ClassOrObjectInvokes): ClassOrObjectUses {
        val uses = invokes.map { it.annotations to HashMap<Set<String>, MutableList<String>>() }.toMap()
        for (invoke in invokes) {
            uses.getValue(invoke.annotations)[invoke.subTypes] = ArrayList()
        }
        return uses
    }

    private fun SubTypesToAnnotations.covers(element: KtClassOrObject): Boolean =
        // annotations set is empty when withSubTypes() method is called, so we don't need to check annotations in this case
        (annotations.isEmpty() || element.getAnnotations(binding, annotations).isNotEmpty()) && element.isSubtypeOf(subTypes, binding)
}
