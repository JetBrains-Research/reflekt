package org.jetbrains.reflekt.plugin.generation

import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.collector.ir.ReflektArgumentsCollectorExtension
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstancesFqNames
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

import java.io.File

class ReflektMetaFileGeneratorExtension(
    private val instancesAnalyzer: IrInstancesAnalyzer,
    private val argumentsCollector: ReflektArgumentsCollectorExtension,
    private val reflektMetaFile: File,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val reflektQueriesArguments = argumentsCollector.getArguments()
        val instancesFqNames = instancesAnalyzer.getInstancesFqNames()
        saveMetaData(reflektQueriesArguments, instancesFqNames)
    }

    // TODO: the arguments are unused since we don't a have IrType serializer yet
    @Suppress("UnusedPrivateMember")
    private fun saveMetaData(libraryArguments: LibraryArguments, instancesFqNames: IrInstancesFqNames) {
        messageCollector?.log("Save Reflekt meta data")
        reflektMetaFile.createNewFile()
        TODO("Implement serialization for IrTypes and save Reflekt Meta")
        // reflektMetaFile.writeBytes(
        // SerializationUtils.encodeInvokes(
        // ReflektInvokesWithPackages(
        // invokes = invokes,
        // packages = files.map { it.packageFqName.asString() }.toSet(),
        // ),
        // ),
        // )
    }

    private fun IrInstancesAnalyzer.getInstancesFqNames() = IrInstancesFqNames.fromIrInstances(this.getIrInstances())
}
