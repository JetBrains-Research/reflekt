package org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.base

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.fullName

abstract class BaseClassInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseInvokesProcessor<ClassOrObjectInvokes>(binding, messageCollector) {
    override val fileToInvokes: HashMap<FileID, ClassOrObjectInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, ClassOrObjectInvokes> {
        processClassOrObjectInvokes(element)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addAll(it)
        }
        return fileToInvokes
    }

}
