package io.reflekt.plugin.analysis.processor

import io.reflekt.plugin.analysis.ReflektInvokes
import io.reflekt.plugin.analysis.ReflektUses
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

// TODO: rename
class UsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : Processor<ReflektUses>(binding) {
    // TODO: rename
    val reflektUses = ReflektUses()

    override fun process(element: KtElement): ReflektUses {
        (element as? KtClass)?.let { processClass(it) }
        (element as? KtObjectDeclaration)?.let { processObject(it) }
        (element as? KtFunction)?.let { processFunction(it) }
        TODO("Not yet implemented")
    }

    private fun processClass(klass: KtClass) {
        TODO("Not yet implemented, update [classes] in [reflektUses]")
    }

    private fun processObject(obj: KtObjectDeclaration) {
        TODO("Not yet implemented, update [objects] in [reflektUses]")
    }

    private fun processFunction(function: KtFunction) {
        TODO("Not yet implemented, update [functions] in [reflektUses]")
    }

    fun shouldRunOn(klass: KtClass): Boolean {
        TODO("Not yet implemented: if [klass] is valid for at least one item from [classes] in [reflektInvokes]")
    }

    fun shouldRunOn(obj: KtObjectDeclaration): Boolean {
        TODO("Not yet implemented: if [obj] is valid for at least one item from [objects] in [reflektInvokes]")
    }

    fun shouldRunOn(function: KtFunction): Boolean = reflektInvokes.functions.map { function.getAnnotations(binding, it) }.any { it.isNotEmpty() }

}
