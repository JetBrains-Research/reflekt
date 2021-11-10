package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.isPublicNotAbstractClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class ClassUsesProcessor(override val binding: BindingContext, reflektInvokes: ReflektInvokes) : BaseUsesProcessor<ClassOrObjectUses>(binding) {
    override val fileToUses: HashMap<FileId, ClassOrObjectUses> = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.classes)

    override fun process(element: KtElement, file: KtFile): HashMap<FileId, ClassOrObjectUses> =
        processClassOrObjectUses(element, file, invokes, fileToUses)

    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass
}
