package org.jetbrains.reflekt.plugin.analysis.processor.source.instances

import org.jetbrains.reflekt.plugin.analysis.processor.*
import org.jetbrains.reflekt.plugin.analysis.processor.isPublicNotAbstractClass
import org.jetbrains.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @property binding
 */
open class ClassOrObjectInstancesProcessor<T : KtClassOrObject>(override val binding: BindingContext) : BaseInstancesProcessor<MutableList<T>>(binding) {
    override val fileToInstances: FileToListInstances<T> = HashMap()

    override fun process(element: KtElement, file: KtFile): FileToListInstances<T> {
        (element as? T)?.let { castedElement ->
            fileToInstances.getOrPut(file.fullName) { ArrayList() }.add(castedElement)
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
