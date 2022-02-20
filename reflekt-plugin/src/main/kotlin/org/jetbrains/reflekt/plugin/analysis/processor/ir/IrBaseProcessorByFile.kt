@file:Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER")

package org.jetbrains.reflekt.plugin.analysis.processor.ir

import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.processor.source.Processor

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile

typealias FileToListIrInstances<T> = HashMap<FileId, MutableList<T>>
typealias ResultToFileSet<T> = HashMap<T, Set<FileId>>
typealias ResultToFilteredInstances<R, I> = HashMap<R, Set<I>>

/**
 * A base class to process [IrElement] by [IrFile].
 *
 * @property fileToCollectedElements stores IR instances by [FileId]
 */
abstract class IrBaseProcessorByFile<T : Any> : Processor<HashMap<FileId, T>, IrElement, IrFile>() {
    abstract val fileToCollectedElements: HashMap<FileId, T>
}

/**
 * A base class to process [IrElement] and extract [T].
 * For each [T] stores the set of [FileId] where the initial [IrElement] were found.
 * Also, for each [T] calculates [E] and store in the [cache], e.g. filter all instances by a Reflekt query
 *
 * @property collectedElements stores a [HashMap] with processed result and a [FileId] set for this result
 * @property cache
 */
abstract class IrBaseProcessorWithCache<T : Any, E : IrElement> : Processor<ResultToFileSet<T>, IrElement, IrFile>() {
    abstract val collectedElements: ResultToFileSet<T>
    abstract val cache: ResultToFilteredInstances<T, E>
}
