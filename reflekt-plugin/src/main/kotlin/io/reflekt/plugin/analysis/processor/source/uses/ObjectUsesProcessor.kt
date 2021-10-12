package io.reflekt.plugin.analysis.processor.source.uses

import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.processor.*
import io.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class ObjectUsesProcessor(override val binding: BindingContext, reflektInvokes: ReflektInvokes, override val messageCollector: MessageCollector?) :
    BaseUsesProcessor<ClassOrObjectUses>(binding, messageCollector) {
    override val fileToUses: HashMap<FileID, ClassOrObjectUses> = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.objects)

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, ClassOrObjectUses> =
        processClassOrObjectUses(element, file, invokes, fileToUses)

    override fun shouldRunOn(element: KtElement) = element.isPublicObject
}
