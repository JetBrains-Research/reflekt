package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.analysis.ReflektInvokes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ObjectUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<ClassOrObjectUses>(binding) {
    override val uses: ClassOrObjectUses = mutableMapOf()

    override fun process(element: KtElement): ClassOrObjectUses {
        // TODO: Not yet implemented
        return uses
    }

    // TODO: Not yet implemented
    private fun isValidObject(obj: KtObjectDeclaration): Boolean = false

    override fun shouldRunOn(element: KtElement) = (element as? KtObjectDeclaration)?.let{ isValidObject(it) } ?: false
}
