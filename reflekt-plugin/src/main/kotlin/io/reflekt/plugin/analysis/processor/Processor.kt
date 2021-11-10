package io.reflekt.plugin.analysis.processor

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @property binding
 */
abstract class Processor<T : Any>(protected open val binding: BindingContext) {
    // Return processed elements by file
    abstract fun process(element: KtElement, file: KtFile): HashMap<FileId, T>

    abstract fun shouldRunOn(element: KtElement): Boolean
}
