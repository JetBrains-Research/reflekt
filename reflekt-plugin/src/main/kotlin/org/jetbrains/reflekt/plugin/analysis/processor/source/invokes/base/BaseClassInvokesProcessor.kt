package org.jetbrains.reflekt.plugin.analysis.processor.source.invokes.base

import org.jetbrains.reflekt.plugin.analysis.models.psi.ClassOrObjectInvokes
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.processor.fullName

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseClassInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseInvokesProcessor<ClassOrObjectInvokes>(binding, messageCollector) {
    override val fileToInvokes: HashMap<FileId, ClassOrObjectInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileId, ClassOrObjectInvokes> {
        processClassOrObjectInvokes(element)?.let { obj ->
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addAll(obj)
        }
        return fileToInvokes
    }
}
