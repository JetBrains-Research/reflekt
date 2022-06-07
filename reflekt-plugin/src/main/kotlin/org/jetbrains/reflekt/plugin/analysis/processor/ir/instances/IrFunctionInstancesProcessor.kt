package org.jetbrains.reflekt.plugin.analysis.processor.ir.instances

import org.jetbrains.reflekt.plugin.analysis.processor.common.isTopLevelPublicFunction
import org.jetbrains.reflekt.plugin.analysis.processor.fullName
import org.jetbrains.reflekt.plugin.analysis.processor.ir.FileToListIrInstances
import org.jetbrains.reflekt.plugin.analysis.processor.ir.IrBaseProcessorByFile

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction

/**
 * Processor for searching public top level functions.
 *
 * @property fileToCollectedElements stores HashMap of IR function instances by files
 */
class IrFunctionInstancesProcessor :
    IrBaseProcessorByFile<MutableList<IrFunction>>() {
    override val fileToCollectedElements: FileToListIrInstances<IrFunction> = HashMap()

    override fun process(element: IrElement, file: IrFile): FileToListIrInstances<IrFunction> {
        (element as? IrFunction)?.let {
            fileToCollectedElements.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToCollectedElements
    }

    override fun shouldRunOn(element: IrElement) = element.isTopLevelPublicFunction
}
