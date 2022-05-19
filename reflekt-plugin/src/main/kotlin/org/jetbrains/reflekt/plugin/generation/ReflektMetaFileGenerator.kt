package org.jetbrains.reflekt.plugin.generation

import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.analysis.serialization.SerializationUtils
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

import java.io.File

/*
 * A class to generate the ReflektMeta file.
 * We use IrGenerationExtension here to register it after collecting all Reflekt arguments.
 */
class ReflektMetaFileGenerator(
    private val instancesAnalyzer: IrInstancesAnalyzer,
    private val reflektQueriesArguments: LibraryArguments,
    private val reflektMetaFile: File,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val instancesFqNames = instancesAnalyzer.getInstancesFqNames()
        saveMetaData(reflektQueriesArguments, instancesFqNames)
    }

    private fun saveMetaData(libraryArguments: LibraryArguments, instancesFqNames: IrInstancesFqNames) {
        messageCollector?.log("Save Reflekt meta data")
        reflektMetaFile.createNewFile()
        reflektMetaFile.writeBytes(
            SerializationUtils.encodeArguments(
                LibraryArgumentsWithInstances(libraryArguments, instancesFqNames),
            ),
        )
    }

    private fun IrInstancesAnalyzer.getInstancesFqNames() = IrInstancesFqNames.fromIrInstances(this.getIrInstances())
}
