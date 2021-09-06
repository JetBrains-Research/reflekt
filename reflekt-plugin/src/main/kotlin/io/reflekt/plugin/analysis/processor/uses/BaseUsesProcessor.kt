package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.SupertypesToAnnotations
import io.reflekt.plugin.analysis.processor.Processor
import io.reflekt.plugin.analysis.processor.fullName
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

//TODO: check code generation case when invokes are not empty but uses are empty and add tests for code generation
abstract class BaseUsesProcessor<Output : Any>(override val binding: BindingContext) : Processor<Output>(binding) {
    // Store uses by file
    abstract val fileToUses: HashMap<String, Output>

    protected fun processClassOrObjectUses(
        element: KtElement, file: KtFile,
        invokes: ClassOrObjectInvokes,
        fileToUses: HashMap<String, ClassOrObjectUses>
    ): HashMap<String, ClassOrObjectUses> {
        (element as? KtClassOrObject)?.let {
            invokes.filter { it.covers(element) }.forEach {
                fileToUses.getOrPut(file.fullName) { HashMap() }.getOrPut(it) { mutableListOf() }.add(element)
            }
        }
        return fileToUses
    }

    // To avoid repeated checks for belonging invokes in different files,
    // we will group invokes by files and process each of them once
    // MutableSet<*> here is ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
    //   or FunctionInvokes = MutableSet<SignatureToAnnotations> MutableSet<*>
    protected fun <T> groupFilesByInvokes(fileToInvokes: HashMap<String, T>): HashMap<T, MutableSet<String>> {
        val filesByInvokes = HashMap<T, MutableSet<String>>()
        fileToInvokes.forEach { (file, invoke) ->
            filesByInvokes.getOrPut(invoke) { mutableSetOf() }.add(file)
        }
        return filesByInvokes
    }

    private fun SupertypesToAnnotations.covers(element: KtClassOrObject): Boolean =
        // annotations set is empty when withSupertypes() method is called, so we don't need to check annotations in this case
        (annotations.isEmpty() || element.getAnnotations(binding, annotations).isNotEmpty()) && element.isSubtypeOf(supertypes, binding)
}
