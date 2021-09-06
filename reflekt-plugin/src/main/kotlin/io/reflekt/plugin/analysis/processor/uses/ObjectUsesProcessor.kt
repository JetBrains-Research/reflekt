package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class ObjectUsesProcessor(override val binding: BindingContext, reflektInvokes: ReflektInvokes) : BaseUsesProcessor<ClassOrObjectUses>(binding) {
    override val fileToUses: HashMap<String, ClassOrObjectUses> = HashMap()
    private val invokes = getGroupedInvokes(reflektInvokes.objects)

    override fun process(element: KtElement, file: KtFile): HashMap<String, ClassOrObjectUses> =
        processClassOrObjectUses(element, file, invokes, fileToUses)

    override fun shouldRunOn(element: KtElement) = element.isPublicObject
}
