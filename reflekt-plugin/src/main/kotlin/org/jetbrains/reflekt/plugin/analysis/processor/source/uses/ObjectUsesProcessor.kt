package org.jetbrains.reflekt.plugin.analysis.processor.source.uses

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.models.ClassOrObjectUses
import org.jetbrains.reflekt.plugin.analysis.models.ReflektInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.*
import org.jetbrains.reflekt.plugin.utils.Util.log

class ObjectUsesProcessor(override val binding: BindingContext, reflektInvokes: ReflektInvokes, override val messageCollector: MessageCollector?) :
    BaseUsesProcessor<ClassOrObjectUses>(binding, messageCollector) {
    override val fileToUses: HashMap<FileID, ClassOrObjectUses> = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.objects)

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, ClassOrObjectUses> =
        processClassOrObjectUses(element, file, invokes, fileToUses)

    override fun shouldRunOn(element: KtElement): Boolean {
        val shouldRunOn = element.isPublicObject
        messageCollector?.log("ObjectUsesProcessor. Element: ${element.text}, should run on $shouldRunOn")
        return shouldRunOn
    }
}
