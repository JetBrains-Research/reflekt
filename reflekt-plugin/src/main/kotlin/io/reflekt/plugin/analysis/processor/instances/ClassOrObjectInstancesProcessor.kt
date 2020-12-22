package io.reflekt.plugin.analysis.processor.instances

import io.reflekt.plugin.analysis.processor.isPublicNotAbstractClass
import io.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext

open class ClassOrObjectInstancesProcessor(override val binding: BindingContext) : BaseInstancesProcessor<List<KtClassOrObject>>(binding) {
    override val instances: MutableList<KtClassOrObject> = ArrayList()

    override fun process(element: KtElement): List<KtClassOrObject> {
        (element as? KtClassOrObject)?.let {
            instances.add(it)
        }
        return instances
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass || element.isPublicObject
}

class ClassInstancesProcessor(override val binding: BindingContext) : ClassOrObjectInstancesProcessor(binding) {
    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass
}

class ObjectInstancesProcessor(override val binding: BindingContext) : ClassOrObjectInstancesProcessor(binding) {
    override fun shouldRunOn(element: KtElement) = element.isPublicObject
}

