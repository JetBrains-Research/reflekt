package org.jetbrains.reflekt.plugin.analysis.processor.ir.instances

import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.processor.source.Processor

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile

typealias FileToListIrInstances<T> = HashMap<FileId, MutableList<T>>

/**
 * A base class to find IR instances of classes, objects, functions, etc
 */
abstract class IrBaseInstancesProcessor<T : Any> : Processor<T, IrElement, IrFile>() {
    // Store IR instances by file
    abstract val fileToIrInstances: HashMap<FileId, T>
}
