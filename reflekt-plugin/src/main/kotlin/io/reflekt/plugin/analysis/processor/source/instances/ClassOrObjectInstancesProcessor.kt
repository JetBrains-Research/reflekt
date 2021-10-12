package io.reflekt.plugin.analysis.processor.source.instances

import io.reflekt.plugin.analysis.processor.*
import io.reflekt.plugin.analysis.processor.isPublicNotAbstractClass
import io.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

open class ClassOrObjectInstancesProcessor<T : KtClassOrObject>(override val binding: BindingContext) : BaseInstancesProcessor<MutableList<T>>(binding) {
    override val fileToInstances: HashMap<FileID, MutableList<T>> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, MutableList<T>> {
        (element as? T)?.let {
            fileToInstances.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToInstances
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass || element.isPublicObject
}

class ClassInstancesProcessor(override val binding: BindingContext) : ClassOrObjectInstancesProcessor<KtClass>(binding) {
    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass
}

class ObjectInstancesProcessor(override val binding: BindingContext) : ClassOrObjectInstancesProcessor<KtObjectDeclaration>(binding) {
    override fun shouldRunOn(element: KtElement) = element.isPublicObject
}

