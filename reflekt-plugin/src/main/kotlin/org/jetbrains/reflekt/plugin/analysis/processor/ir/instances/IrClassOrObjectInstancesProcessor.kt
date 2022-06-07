package org.jetbrains.reflekt.plugin.analysis.processor.ir.instances

import org.jetbrains.reflekt.plugin.analysis.processor.common.isPublicNotAbstractClass
import org.jetbrains.reflekt.plugin.analysis.processor.common.isPublicObject
import org.jetbrains.reflekt.plugin.analysis.processor.fullName
import org.jetbrains.reflekt.plugin.analysis.processor.ir.FileToListIrInstances
import org.jetbrains.reflekt.plugin.analysis.processor.ir.IrBaseProcessorByFile

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile

/**
 * A base class with common functionality for searching classes and objects.
 *
 * @property fileToCollectedElements stores HashMap of IR instances (classes or objects) by files
 */
abstract class IrClassOrObjectInstancesProcessor<T : IrClass> : IrBaseProcessorByFile<MutableList<IrClass>>() {
    override val fileToCollectedElements: FileToListIrInstances<IrClass> = HashMap()
    override fun process(element: IrElement, file: IrFile): FileToListIrInstances<IrClass> {
        (element as? T)?.let {
            fileToCollectedElements.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToCollectedElements
    }
}

/**
 * Processor for searching public not abstract classes.
 */
class IrClassInstancesProcessor : IrClassOrObjectInstancesProcessor<IrClass>() {
    override fun shouldRunOn(element: IrElement) = element.isPublicNotAbstractClass
}

/**
 * Processor for searching public objects.
 */
class IrObjectInstancesProcessor : IrClassOrObjectInstancesProcessor<IrClass>() {
    override fun shouldRunOn(element: IrElement) = element.isPublicObject
}
