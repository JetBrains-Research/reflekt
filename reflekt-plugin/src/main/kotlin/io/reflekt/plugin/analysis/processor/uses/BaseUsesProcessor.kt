package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.SupertypesToAnnotations
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
            invokes.filter { it.covers(element) }.forEach {
                uses.getValue(it).add(element)
            }
        }
        return uses
    }

    protected fun initClassOrObjectUses(invokes: ClassOrObjectInvokes): ClassOrObjectUses =
        invokes.map { it to ArrayList<KtClassOrObject>() }.toMap()

    private fun SupertypesToAnnotations.covers(element: KtClassOrObject): Boolean =
        // annotations set is empty when withSupertypes() method is called, so we don't need to check annotations in this case
        (annotations.isEmpty() || element.getAnnotations(binding, annotations).isNotEmpty()) && element.isSubtypeOf(supertypes, binding)
}
