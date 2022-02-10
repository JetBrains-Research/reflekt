package org.jetbrains.reflekt.plugin.analysis.processor.source.uses

import org.jetbrains.reflekt.plugin.analysis.models.psi.ClassOrObjectUses
import org.jetbrains.reflekt.plugin.analysis.models.psi.ReflektInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.*
import org.jetbrains.reflekt.plugin.analysis.processor.common.isPublicNotAbstractClass
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @param reflektInvokes
 *
 * @property binding
 * @property messageCollector
 */
class ClassUsesProcessor(
    override val binding: BindingContext,
    reflektInvokes: ReflektInvokes,
    override val messageCollector: MessageCollector?) :
    BaseUsesProcessor<ClassOrObjectUses>(binding, messageCollector) {
    override val fileToUses: HashMap<FileId, ClassOrObjectUses> = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.classes)

    override fun process(element: KtElement, file: KtFile): HashMap<FileId, ClassOrObjectUses> =
        processClassOrObjectUses(element, file, invokes, fileToUses)

    override fun shouldRunOn(element: KtElement): Boolean {
        val shouldRunOn = element.isPublicNotAbstractClass
        messageCollector?.log("ClassUsesProcessor. Element: ${element.text}, should run on $shouldRunOn")
        return shouldRunOn
    }
}
