package org.jetbrains.reflekt.plugin.analysis.processor.source

import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @property binding
 * @property messageCollector
 */
abstract class Processor<T : Any>(protected open val binding: BindingContext, protected open val messageCollector: MessageCollector? = null) {
    // Return processed elements by file
    abstract fun process(element: KtElement, file: KtFile): HashMap<FileId, T>

    abstract fun shouldRunOn(element: KtElement): Boolean
}
