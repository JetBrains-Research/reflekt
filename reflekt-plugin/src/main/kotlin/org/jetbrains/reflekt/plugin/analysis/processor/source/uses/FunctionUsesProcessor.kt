package org.jetbrains.reflekt.plugin.analysis.processor.source.uses

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.processor.*
import org.jetbrains.reflekt.plugin.analysis.psi.annotation.getAnnotations
import org.jetbrains.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.reflekt.plugin.utils.Util.log


class FunctionUsesProcessor(override val binding: BindingContext, reflektInvokes: ReflektInvokes, override val messageCollector: MessageCollector?) :
    BaseUsesProcessor<FunctionUses>(binding, messageCollector) {
    override val fileToUses: HashMap<FileID, FunctionUses> = HashMap()
    private val invokes = getInvokesGroupedByFiles(reflektInvokes.functions)

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, FunctionUses> {
        (element as? KtNamedFunction)?.let {
            invokes.filter { it.covers(element) }.forEach {
                fileToUses.getOrPut(file.fullName) { HashMap() }.getOrPut(it) { mutableListOf() }.add(element)
            }
        }
        return fileToUses
    }

    // TODO: how can we return the member functions??
    override fun shouldRunOn(element: KtElement): Boolean {
        val shouldRunOn = element.isTopLevelPublicFunction && !element.isMainFunction
        messageCollector?.log("FunctionUsesProcessor. Element: $element with text ${element.text}, should run on $shouldRunOn")
        return shouldRunOn
    }

    private fun SignatureToAnnotations.covers(function: KtNamedFunction): Boolean {
        return (annotations.isEmpty() || function.getAnnotations(binding, annotations).isNotEmpty()) &&
            function.toParameterizedType(binding)?.isSubtypeOf(signature!!) ?: false
    }
}
