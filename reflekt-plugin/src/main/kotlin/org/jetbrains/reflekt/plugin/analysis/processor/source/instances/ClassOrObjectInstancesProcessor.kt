package org.jetbrains.reflekt.plugin.analysis.processor.source.instances

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.models.TypeInstances
import org.jetbrains.reflekt.plugin.analysis.processor.*

open class ClassOrObjectInstancesProcessor<T : KtClassOrObject>(override val binding: BindingContext) : BaseInstancesProcessor<TypeInstances<T>>(binding) {
    override val fileToInstances: HashMap<FileID, TypeInstances<T>> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, TypeInstances<T>> {
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

