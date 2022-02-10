package org.jetbrains.reflekt.plugin.analysis.processor.ir.instances

import org.jetbrains.reflekt.plugin.analysis.processor.common.isTopLevelPublicFunction
import org.jetbrains.reflekt.plugin.analysis.processor.fullName

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction

/**
 * Processor for searching public top level functions
 */
class IrFunctionInstancesProcessor :
    IrBaseInstancesProcessor<MutableList<IrFunction>>() {
    override val fileToIrInstances: FileToListIrInstances<IrFunction> = HashMap()

    override fun process(element: IrElement, file: IrFile): FileToListIrInstances<IrFunction> {
        (element as? IrFunction)?.let {
            fileToIrInstances.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToIrInstances
    }

    override fun shouldRunOn(element: IrElement) = element.isTopLevelPublicFunction
}
