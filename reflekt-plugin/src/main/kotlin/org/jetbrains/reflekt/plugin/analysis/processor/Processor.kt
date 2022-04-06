package org.jetbrains.reflekt.plugin.analysis.processor

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * A base class for elements processors, e.g. process IrElement or KtElement
 *
 * @property binding
 * @property messageCollector
 */
// TODO: delete BindingContext
abstract class Processor<T : Any, E : Any, F : Any>(protected open val binding: BindingContext? = null, protected open val messageCollector: MessageCollector? = null) {
    // Return processed elements by file
    abstract fun process(element: E, file: F): T

    abstract fun shouldRunOn(element: E): Boolean
}
