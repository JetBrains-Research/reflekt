package io.reflekt.plugin.analysis.processor

import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.analysis.ReflektInvokes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ObjectProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : Processor<ClassOrObjectUses>(binding) {
    val objects: ClassOrObjectUses = mutableMapOf()

    override fun process(element: KtElement): ClassOrObjectUses {
        TODO("Not yet implemented")
    }

    private fun isValidObject(obj: KtObjectDeclaration): Boolean = TODO("Not yet implemented")

    override fun shouldRunOn(element: KtElement) = (element as? KtObjectDeclaration)?.let{ isValidObject(it) } ?: false
}
