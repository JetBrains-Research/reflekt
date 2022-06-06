package org.jetbrains.reflekt.plugin.analysis.collector.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.nameForIrSerialization
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName

import java.io.File

/**
 * A collector for searching and collecting all classes, objects, and functions in the project.
 *
 * @param irInstancesAnalyzer analyzer that check if the current IR element satisfy a condition,
 *  e.g. is a top level function
 * @param messageCollector
 */
class InstancesCollector(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    messageCollector: MessageCollector? = null,
) : BaseCollector(irInstancesAnalyzer, messageCollector) {
    override fun visitDeclaration(declaration: IrDeclarationBase) {
        messageCollector?.log("Start checking declaration: ${declaration.nameForIrSerialization}")
        irInstancesAnalyzer.process(declaration, declaration.file)
        messageCollector?.log("Finish checking declaration: ${declaration.nameForIrSerialization}")
        super.visitDeclaration(declaration)
    }
}

/**
 * A compiler plugin extension for searching and collection all classes, objects, and functions.
 */
class InstancesCollectorExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryArgumentsWithInstances: LibraryArgumentsWithInstances,
    private val reflektMetaFilesFromLibraries: Set<File>,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    // TODO: can we avoid making a copy here? (using var and make a copy later, e.g. 60-61 code rows)
    private var libraryArguments = LibraryArguments()
    private var irInstancesFqNames = IrInstancesFqNames()

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        extractInstancesFromLibraries(pluginContext)
        moduleFragment.acceptChildrenVoid(InstancesCollector(irInstancesAnalyzer, messageCollector))
    }

    private fun extractInstancesFromLibraries(pluginContext: IrPluginContext) {
        for (metaFile in reflektMetaFilesFromLibraries) {
            val currentLibraryArgumentsWithInstances = SerializationUtils.decodeArguments(metaFile.readBytes(), pluginContext)
            libraryArguments = libraryArguments.merge(currentLibraryArgumentsWithInstances.libraryArguments)
            irInstancesFqNames = irInstancesFqNames.merge(currentLibraryArgumentsWithInstances.instances)
        }
        libraryArgumentsWithInstances.replace(libraryArguments, irInstancesFqNames)
    }
}

class ExternalLibraryInstancesCollectorExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val irInstancesFqNames: IrInstancesFqNames,
) : IrGenerationExtension {
    private val externalLibraryId = "EXTERNAL_LIBRARY"

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val classes = findIrClasses(irInstancesFqNames.classes, pluginContext::referenceClass)
        val objects = findIrClasses(irInstancesFqNames.objects, pluginContext::referenceClass)
        val functions = findIrFunctions(irInstancesFqNames.functions, pluginContext::referenceFunctions)
        irInstancesAnalyzer.addDeclarations(externalLibraryId, classes, objects, functions)
    }

    private fun findIrClasses(
        fqNamesStr: List<String>,
        referenceDeclaration: (FqName) -> IrClassSymbol?,
    ) = fqNamesStr.mapNotNull { referenceDeclaration(FqName(it))?.owner }

    private fun findIrFunctions(
        fqNamesStr: List<String>,
        referenceDeclaration: (FqName) -> Collection<IrFunctionSymbol>?,
    ) = fqNamesStr.mapNotNull { referenceDeclaration(FqName(it)) }.flatten().map { it.owner }
}
