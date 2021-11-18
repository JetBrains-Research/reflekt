package org.jetbrains.reflekt.plugin.analysis.processor.source.instances

import org.jetbrains.reflekt.plugin.analysis.processor.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @property binding
 */
class FunctionInstancesProcessor(override val binding: BindingContext) : BaseInstancesProcessor<MutableList<KtNamedFunction>>(binding) {
    override val fileToInstances: FileToListInstances<KtNamedFunction> = HashMap()

    override fun process(element: KtElement, file: KtFile): FileToListInstances<KtNamedFunction> {
        (element as? KtNamedFunction)?.let {
            fileToInstances.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToInstances
    }

    // TODO: how can we return the member functions??
    override fun shouldRunOn(element: KtElement) = element.isTopLevelPublicFunction
}
