package org.jetbrains.reflekt.plugin.analysis.processor.source

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.processor.FileID

abstract class Processor<Output : Any>(protected open val binding: BindingContext, protected open val messageCollector: MessageCollector? = null) {
    // Return processed elements by file
    abstract fun process(element: KtElement, file: KtFile): HashMap<FileID, Output>

    abstract fun shouldRunOn(element: KtElement): Boolean
}
