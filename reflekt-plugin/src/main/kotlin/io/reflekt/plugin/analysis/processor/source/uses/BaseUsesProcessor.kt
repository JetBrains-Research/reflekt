package io.reflekt.plugin.analysis.processor.source.uses

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.*
import io.reflekt.plugin.analysis.processor.source.Processor
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseUsesProcessor<Output : Any>(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    Processor<Output>(binding, messageCollector) {
    // Store uses by file
    abstract val fileToUses: HashMap<FileID, Output>

    protected fun processClassOrObjectUses(
        element: KtElement,
        file: KtFile,
        invokes: ClassOrObjectInvokes,
        fileToUses: HashMap<FileID, ClassOrObjectUses>
    ): HashMap<FileID, ClassOrObjectUses> {
        (element as? KtClassOrObject)?.let {
            invokes.filter { it.covers(element) }.forEach {
                fileToUses.getOrPut(file.fullName) { HashMap() }.getOrPut(it) { mutableListOf() }.add(element)
            }
        }
        return fileToUses
    }

    private fun SupertypesToAnnotations.covers(element: KtClassOrObject): Boolean =
        // annotations set is empty when withSupertypes() method is called, so we don't need to check annotations in this case
        (annotations.isEmpty() || element.getAnnotations(binding, annotations).isNotEmpty()) && element.isSubtypeOf(supertypes, binding)
}