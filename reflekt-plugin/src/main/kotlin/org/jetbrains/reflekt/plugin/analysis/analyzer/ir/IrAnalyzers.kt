package org.jetbrains.reflekt.plugin.analysis.analyzer.ir

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import org.jetbrains.reflekt.plugin.analysis.processor.ir.instances.*
import org.jetbrains.reflekt.plugin.analysis.processor.source.Processor

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile

typealias IrElementProcessor = Processor<*, IrElement, IrFile>

abstract class IrAnalyzer {
    abstract val processors: List<IrElementProcessor>

    fun process(element: IrElement, file: IrFile) {
        processors.filter { it.shouldRunOn(element) }.forEach { it.process(element, file) }
    }
}

class IrInstancesAnalyzer : IrAnalyzer() {
    override val processors: List<IrElementProcessor> =
        listOf(IrClassInstancesProcessor(), IrObjectInstancesProcessor(), IrFunctionInstancesProcessor())

    // TODO: should we store entities by files??
    fun getIrInstances() = IrInstances(
        classes = processors.filterIsInstance<IrClassInstancesProcessor>().first().fileToIrInstances
            .values.flatten(),
        objects = processors.filterIsInstance<IrObjectInstancesProcessor>().first().fileToIrInstances
            .values.flatten(),
        functions = processors.filterIsInstance<IrFunctionInstancesProcessor>().first().fileToIrInstances
            .values.flatten(),
    )
}
