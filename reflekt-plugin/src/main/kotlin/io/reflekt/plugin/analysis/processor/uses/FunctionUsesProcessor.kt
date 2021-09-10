package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.*
import io.reflekt.plugin.analysis.processor.fullName
import io.reflekt.plugin.analysis.processor.isPublicFunction
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf


class FunctionUsesProcessor(override val binding: BindingContext, reflektInvokes: ReflektInvokes) : BaseUsesProcessor<FunctionUses>(binding) {
    override val fileToUses: HashMap<String, FunctionUses> = HashMap()
    private val invokes = getGroupedInvokes(reflektInvokes.functions)

    override fun process(element: KtElement, file: KtFile): HashMap<String, FunctionUses> {
        (element as? KtNamedFunction)?.let {
            invokes.filter { it.covers(element) }.forEach {
                fileToUses.getOrPut(file.fullName) { HashMap() }.getOrPut(it) { mutableListOf() }.add(element)
            }
        }
        return fileToUses
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicFunction && !element.isMainFunction

    private fun SignatureToAnnotations.covers(function: KtNamedFunction): Boolean {
        return (annotations.isEmpty() || function.getAnnotations(binding, annotations).isNotEmpty()) &&
            function.toParameterizedType(binding)?.isSubtypeOf(signature) ?: false
    }
}
