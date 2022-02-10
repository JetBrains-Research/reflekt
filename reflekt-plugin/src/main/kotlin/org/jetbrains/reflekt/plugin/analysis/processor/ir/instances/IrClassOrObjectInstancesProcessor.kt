package org.jetbrains.reflekt.plugin.analysis.processor.ir.instances

import org.jetbrains.reflekt.plugin.analysis.processor.common.isPublicNotAbstractClass
import org.jetbrains.reflekt.plugin.analysis.processor.common.isPublicObject
import org.jetbrains.reflekt.plugin.analysis.processor.fullName

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile

/**
 * A base class with common functionality for searching classes and objects
 */
abstract class IrClassOrObjectInstancesProcessor<T : IrClass> : IrBaseInstancesProcessor<MutableList<IrClass>>() {
    override val fileToIrInstances: FileToListIrInstances<IrClass> = HashMap()
    override fun process(element: IrElement, file: IrFile): FileToListIrInstances<IrClass> {
        (element as? T)?.let {
            fileToIrInstances.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToIrInstances
    }
}

/**
 * Processor for searching public not abstract classes
 */
class IrClassInstancesProcessor : IrClassOrObjectInstancesProcessor<IrClass>() {
    override fun shouldRunOn(element: IrElement) = element.isPublicNotAbstractClass
}

/**
 * Processor for searching public objects
 */
class IrObjectInstancesProcessor : IrClassOrObjectInstancesProcessor<IrClass>() {
    override fun shouldRunOn(element: IrElement) = element.isPublicObject
}
