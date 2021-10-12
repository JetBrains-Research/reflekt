package io.reflekt.plugin.analysis.processor.source.instances

import io.reflekt.plugin.analysis.processor.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class FunctionInstancesProcessor(override val binding: BindingContext) : BaseInstancesProcessor<MutableList<KtNamedFunction>>(binding) {
    override val fileToInstances: HashMap<FileID, MutableList<KtNamedFunction>> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, MutableList<KtNamedFunction>> {
        (element as? KtNamedFunction)?.let {
            fileToInstances.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToInstances
    }

    // TODO: how can we return the member functions??
    override fun shouldRunOn(element: KtElement) = element.isTopLevelPublicFunction
}
