@file:Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER")

package org.jetbrains.reflekt.plugin.analysis.analyzer.ir

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import org.jetbrains.reflekt.plugin.analysis.processor.ir.instances.*
import org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments.*
import org.jetbrains.reflekt.plugin.analysis.processor.source.Processor

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall

typealias IrElementProcessor = Processor<*, IrElement, IrFile>

/**
 * A base class for IR analyzers.
 *
 * @property processors
 */
abstract class IrAnalyzer {
    abstract val processors: List<IrElementProcessor>

    /**
     * Filter processors that should be run on the [element] and process it.
     *
     * @param element
     * @param file [IrFile] with [element]
     */
    fun process(element: IrElement, file: IrFile) {
        processors.filter { it.shouldRunOn(element) }.forEach { it.process(element, file) }
    }
}

/**
 * Collects all instances.
 */
class IrInstancesAnalyzer : IrAnalyzer() {
    private val classProcessor = IrClassInstancesProcessor()
    private val objectProcessor = IrObjectInstancesProcessor()
    private val functionProcessor = IrFunctionInstancesProcessor()
    override val processors: List<IrElementProcessor> =
        listOf(classProcessor, objectProcessor, functionProcessor)

    // TODO: should we store entities by files??
    fun getIrInstances() = IrInstances(
        classes = classProcessor.fileToCollectedElements.values.flatten(),
        objects = objectProcessor.fileToCollectedElements.values.flatten(),
        functions = functionProcessor.fileToCollectedElements.values.flatten(),
    )
}

/**
 * Collects Reflekt queries arguments for each valid [IrElement]
 *  and filters [IrInstances] according these arguments.
 */
class IrReflektQueriesAnalyzer(irInstances: IrInstances, context: IrPluginContext) : IrAnalyzer() {
    val classProcessor = IrClassArgumentProcessor(irInstances.classes, context)
    val objectProcessor = IrObjectArgumentProcessor(irInstances.objects, context)
    val functionProcessor = IrFunctionArgumentProcessor(irInstances.functions, context)
    override val processors: List<IrElementProcessor> =
        listOf(classProcessor, objectProcessor, functionProcessor)

    /**
     * Checks if the [element] is a Reflekt call, collects all query arguments,
     *  and filter [IrInstances] according them.
     *
     * @param element
     * @param file [IrFile] with [element]
     */
    fun processWithCurrentResult(element: IrElement, file: IrFile): List<IrDeclaration> {
        if (element !is IrCall) {
            return emptyList()
        }
        return processors.filter { it.shouldRunOn(element) }.mapNotNull {
            (it as? IrReflektArgumentProcessor<*, *>)?.processWithCurrentResult(element, file)
        }.flatten()
    }
}
