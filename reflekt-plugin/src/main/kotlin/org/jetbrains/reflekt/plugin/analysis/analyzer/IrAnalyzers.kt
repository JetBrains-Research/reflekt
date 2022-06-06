@file:Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.analysis.analyzer

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.reflekt.plugin.analysis.processor.ir.IrBaseProcessorByFile
import org.jetbrains.reflekt.plugin.analysis.processor.ir.instances.*
import org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments.*

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

    fun addDeclarations(
        fileId: FileId,
        classes: List<IrClass>,
        objects: List<IrClass>,
        functions: List<IrFunction>,
    ) {
        classProcessor.addIrClassesToProcessor(fileId, classes)
        objectProcessor.addIrClassesToProcessor(fileId, objects)
        functionProcessor.fileToCollectedElements.getOrPut(fileId) { mutableListOf() }.addAll(functions)
    }

    // TODO: can we unify IrClass and IrFunction?
    @Suppress("TYPE_ALIAS")
    private fun IrBaseProcessorByFile<MutableList<IrClass>>.addIrClassesToProcessor(fileId: FileId, classes: List<IrClass>) {
        this.fileToCollectedElements.getOrPut(fileId) { mutableListOf() }.addAll(classes)
    }
}

/**
 * Collects Reflekt queries arguments for each valid [IrElement]
 *  and filters [IrInstances] according these arguments.
 */
class IrReflektQueriesAnalyzer(irInstances: IrInstances, context: IrPluginContext) : IrAnalyzer() {
    private val classProcessor = IrClassArgumentProcessor(irInstances.classes, context)
    private val objectProcessor = IrObjectArgumentProcessor(irInstances.objects, context)
    private val functionProcessor = IrFunctionArgumentProcessor(irInstances.functions, context)
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
        return processors
            .filter { it.shouldRunOn(element) }
            .mapNotNull { (it as? IrReflektArgumentProcessor<*, *>)?.processWithCurrentResult(element, file) }
            .flatten()
    }

    fun filterInstancesByArguments(reflektQueryArguments: ReflektQueryArguments, instancesKind: ReflektEntity) = when (instancesKind) {
        ReflektEntity.CLASSES -> classProcessor.filterInstancesOrGetFromCache(reflektQueryArguments as SupertypesToAnnotations)
        ReflektEntity.OBJECTS -> objectProcessor.filterInstancesOrGetFromCache(reflektQueryArguments as SupertypesToAnnotations)
        ReflektEntity.FUNCTIONS -> functionProcessor.filterInstancesOrGetFromCache(reflektQueryArguments as SignatureToAnnotations)
    }

    fun parseReflektQueriesArguments(element: IrElement): ReflektQueryArguments? {
        if (element !is IrCall) {
            return null
        }
        val arguments = processors.filter { it.shouldRunOn(element) }.mapNotNull {
            (it as? IrReflektArgumentProcessor<*, *>)?.extractQueryArguments(element)
        }
        // Each processor handles only one type of the Reflekt query.
        // E.g. [classProcessor] can handle only queries for classes, [objectProcessor] - only for objects and so on
        require(arguments.size <= 1) { "Collect several types of reflekt query arguments from one call!" }
        return arguments.firstOrNull()
    }
}
