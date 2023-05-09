package org.jetbrains.reflekt.plugin.analysis.collector.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.*

import java.io.File

/**
 * A collector for searching and collecting all classes, objects, and functions in the project.
 *
 * @param irInstancesAnalyzer the analyzer that checks if the current IR element satisfies to a condition like being a top level function.
 * @param messageCollector
 */
class InstancesCollector(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    messageCollector: MessageCollector? = null,
) : BaseCollector(irInstancesAnalyzer, messageCollector) {
    override fun visitDeclaration(declaration: IrDeclarationBase) {
        messageCollector?.log("Start checking declaration: ${(declaration as? IrDeclarationWithName)?.name}")
        irInstancesAnalyzer.process(declaration, declaration.file)
        messageCollector?.log("Finish checking declaration: ${(declaration as? IrDeclarationWithName)?.name}")
        super.visitDeclaration(declaration)
    }
}

/**
 * A compiler plugin extension for searching and collecting all classes, objects, and functions.
 */
class InstancesCollectorExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryArgumentsWithInstances: LibraryArgumentsWithInstances,
    private val reflektMetaFilesFromLibraries: Set<File>,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    // TODO: can we avoid making a copy here? (using var and make a copy later, e.g. 60-61 code rows)
    private var libraryArguments = LibraryArguments()
    private var irInstancesIds = IrInstancesIds()

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        extractInstancesFromLibraries(pluginContext)
        moduleFragment.acceptChildrenVoid(InstancesCollector(irInstancesAnalyzer, messageCollector))
    }

    private fun extractInstancesFromLibraries(pluginContext: IrPluginContext) {
        for (metaFile in reflektMetaFilesFromLibraries) {
            val currentLibraryArgumentsWithInstances = SerializationUtils.decodeArguments(metaFile.readBytes(), pluginContext)
            libraryArguments = libraryArguments.merge(currentLibraryArgumentsWithInstances.libraryArguments)
            irInstancesIds = irInstancesIds.merge(currentLibraryArgumentsWithInstances.instances)
        }
        libraryArgumentsWithInstances.replace(libraryArguments, irInstancesIds)
    }
}

class ExternalLibraryInstancesCollectorExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val irInstancesIds: IrInstancesIds,
) : IrGenerationExtension {
    private val externalLibraryId = "EXTERNAL_LIBRARY"

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val classes = findIrClasses(irInstancesIds.classes, pluginContext)
        val objects = findIrClasses(irInstancesIds.objects, pluginContext)
        val functions = findIrFunctions(irInstancesIds.functions, pluginContext)
        irInstancesAnalyzer.addDeclarations(externalLibraryId, classes, objects, functions)
    }

    private fun findIrClasses(fqNamesStr: List<ClassId>, pluginContext: IrPluginContext) =
        fqNamesStr.mapNotNull { pluginContext.referenceClass(it)?.owner }

    private fun findIrFunctions(fqNamesStr: List<CallableId>, pluginContext: IrPluginContext) =
        fqNamesStr.map { pluginContext.referenceFunctions(it) }.flatten().map { it.owner }
}
