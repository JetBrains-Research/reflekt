package org.jetbrains.reflekt.plugin.analysis.processor.source.instances

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.models.FunctionInstances
import org.jetbrains.reflekt.plugin.analysis.processor.*

class FunctionInstancesProcessor(override val binding: BindingContext) : BaseInstancesProcessor<FunctionInstances>(binding) {
    override val fileToInstances: HashMap<FileID, FunctionInstances> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, FunctionInstances> {
        (element as? KtNamedFunction)?.let {
            fileToInstances.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToInstances
    }

    // TODO: how can we return the member functions??
    override fun shouldRunOn(element: KtElement) = element.isTopLevelPublicFunction
}
