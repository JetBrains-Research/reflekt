package org.jetbrains.reflekt.plugin.analysis.processor.source

import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

typealias KtElementProcessor = Processor<*, KtElement, KtFile>

/**
 * A base class for elements processors, e.g. process IrElement or KtElement
 *
 * @property binding
 * @property messageCollector
 */
// TODO: delete BindingContext
abstract class Processor<T : Any, E : Any, F : Any>(protected open val binding: BindingContext? = null, protected open val messageCollector: MessageCollector? = null) {
    // Return processed elements by file
    abstract fun process(element: E, file: F): HashMap<FileId, T>

    abstract fun shouldRunOn(element: E): Boolean
}
