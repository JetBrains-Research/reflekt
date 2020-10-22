package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.analysis.ReflektInvokes
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.resolve.BindingContext

class ClassUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<ClassOrObjectUses>(binding) {
    override val uses: ClassOrObjectUses = HashMap()

    override fun process(element: KtElement): ClassOrObjectUses = processClassOrObjectUses(element, reflektInvokes.classes, uses)

    override fun shouldRunOn(element: KtElement) = element is KtClass && element.isPublic && !element.isAbstract()
}
