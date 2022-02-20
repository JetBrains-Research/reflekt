@file:Suppress("KDOC_NO_EMPTY_TAGS", "PACKAGE_NAME_INCORRECT_CASE")

package org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments

import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.processor.fullName
import org.jetbrains.reflekt.plugin.analysis.processor.ir.*
import org.jetbrains.reflekt.plugin.generation.common.ReflektInvokeParts

import org.jetbrains.kotlin.backend.jvm.codegen.AnnotationCodegen.Companion.annotationClass
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable

typealias ReflektArgumentsToFileSet<T> = ResultToFileSet<T>
typealias ReflektArgumentsCache<T, E> = ResultToFilteredInstances<T, E>

/**
 * A base class to extract arguments from the Reflekt queries.
 *
 * @property collectedElements
 * @property cache
 * @property reflektEntity stores the type of the [ReflektEntity] to define the type of the query
 *  (e.g. for functions, or for classes)
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER")
abstract class IrReflektArgumentProcessor<R : Any, T : IrDeclaration> :
    IrBaseProcessorWithCache<R, T>() {
    override val collectedElements: ReflektArgumentsToFileSet<R> = HashMap()
    override val cache: ReflektArgumentsCache<R, T> = HashMap()
    abstract val reflektEntity: ReflektEntity

    /**
     * Process [element] and store the result in [collectedElements] and [cache].
     *
     * @param element
     * @param file [IrFile] with [element]
     * @return [collectedElements] with results for each [IrFile]
     */
    final override fun process(element: IrElement, file: IrFile): ReflektArgumentsToFileSet<R> {
        (element as? IrCall)?.let {
            processWithCurrentResult(element, file)
        }
        return collectedElements
    }

    /**
     * Process [element] and store the result in [collectedElements] and [cache].
     *
     * @param element
     * @param file [IrFile] with [element]
     * @return result of processing that will be saved in the [cache] for the [element]
     */
    fun processWithCurrentResult(element: IrCall, file: IrFile): Set<T> {
        // TODO: should throw an error if the arguments were not parsed?
        val queryArguments = element.collectQueryArguments() ?: return emptySet()
        collectedElements.getOrPut(queryArguments) { setOf(file.fullName) }
        // If we have not filtered instances by the queryArguments call [filterInstances],
        // else return the cached result
        return cache.getOrPut(queryArguments) { filterInstances(queryArguments) }
    }

    /**
     * Collects all query arguments from a Reflekt query, e.g. a set of supertypes and a set of annotations.
     *
     * @return
     */
    protected abstract fun IrCall.collectQueryArguments(): R?

    /**
     * Filters all instances (e.g. all classes from the project and libraries) by the [queryArguments].
     *
     * @param queryArguments
     * @return
     */
    protected abstract fun filterInstances(queryArguments: R): Set<T>

    final override fun shouldRunOn(element: IrElement): Boolean = (element as? IrCall)?.let {
        element.getReflektInvokeParts()?.let {
            it.entityType == reflektEntity
        } ?: false
    } ?: false

    /**
     * Check if the [T] element has at least one annotation from the [annotationsFqNames].
     * If the [annotationsFqNames] is empty the Reflekt query does not have this argument and we should return {@code true}
     *
     * @param annotationsFqNames
     * @return
     */
    protected fun T.hasAnnotationFrom(annotationsFqNames: Set<String>): Boolean {
        if (annotationsFqNames.isEmpty()) {
            return true
        }
        if (this.annotations.isEmpty()) {
            return false
        }
        return annotationsFqNames.any { it in this.annotations.mapNotNull { it.annotationClass.fqNameWhenAvailable?.toString() } }
    }
}

// TODO: rename after renaming of ReflektInvokeParts

/**
 * Parses [IrCall] to extract [ReflektInvokeParts] if it is possible.
 * Returns {@code null otherwise}
 *
 * @return
 */
fun IrCall.getReflektInvokeParts(): ReflektInvokeParts? {
    val function = this.symbol.owner
    val expressionFqName = function.fqNameWhenAvailable?.toString() ?: return null
    return ReflektInvokeParts.parse(expressionFqName)
}
