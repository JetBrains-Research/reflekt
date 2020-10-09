package io.reflekt.plugin.analysis.processor

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

abstract class Processor<Output : Any>(protected open val binding: BindingContext){
    abstract fun process(element: KtElement): Output

    abstract fun shouldRunOn(element: KtElement): Boolean
}
