package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.processor.*
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseUsesProcessor<T : Any>(override val binding: BindingContext) : Processor<T>(binding) {
    // Store uses by file
    abstract val fileToUses: HashMap<FileId, T>

    protected fun processClassOrObjectUses(
        element: KtElement,
        file: KtFile,
        invokes: ClassOrObjectInvokes,
        fileToUses: HashMap<FileId, ClassOrObjectUses>,
    ): HashMap<FileId, ClassOrObjectUses> {
        (element as? KtClassOrObject)?.let {
            invokes.filter { it.isCovering(element) }.forEach {
                fileToUses.getOrPut(file.fullName) { HashMap() }.getOrPut(it) { mutableListOf() }.add(element)
            }
        }
        return fileToUses
    }

    // To avoid repeated checks for belonging invokes in different files,
    // we will group files by invokes and process each of them once
    // MutableSet<*> here is ClassOrObjectInvokes = MutableSet<SupertypesToAnnotations>
    // or FunctionInvokes = MutableSet<SignatureToAnnotations> MutableSet<*>
    @Suppress("TYPE_ALIAS")
    private fun <T> groupFilesByInvokes(fileToInvokes: HashMap<FileId, T>): HashMap<T, MutableSet<String>> {
        val filesByInvokes = HashMap<T, MutableSet<FileId>>()
        fileToInvokes.forEach { (file, invoke) ->
            filesByInvokes.getOrPut(invoke) { mutableSetOf() }.add(file)
        }
        return filesByInvokes
    }

    protected fun <K, V : MutableSet<K>> getInvokesGroupedByFiles(fileToInvokes: HashMap<FileId, V>) =
        groupFilesByInvokes(fileToInvokes).keys.flatten().toMutableSet()

    private fun SupertypesToAnnotations.isCovering(element: KtClassOrObject): Boolean =
        // annotations set is empty when withSupertypes() method is called, so we don't need to check annotations in this case
        (annotations.isEmpty() || element.getAnnotations(binding, annotations).isNotEmpty()) && element.isSubtypeOf(supertypes, binding)
}
