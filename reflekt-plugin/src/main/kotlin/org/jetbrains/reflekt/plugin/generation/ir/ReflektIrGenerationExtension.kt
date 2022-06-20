package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments

/**
 * Replaces Reflekt invoke calls with their results.
 *
 * @property irInstancesAnalyzer to get project instances
 * @property libraryArguments
 * @property messageCollector
 */
class ReflektIrGenerationExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryArguments: LibraryArguments,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    /**
     * Replaces IR in the Reflekt queries to the list of the found entities.
     */
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irInstances = irInstancesAnalyzer.getIrInstances()
        if (irInstances.isEmpty()) {
            return
        }
        val storageClassGenerator = StorageClassGenerator(pluginContext)
        val transformer = ReflektIrTransformer(pluginContext, irInstances, libraryArguments, messageCollector, storageClassGenerator)
        moduleFragment.transform(transformer, null)
        storageClassGenerator.contributeInitializers(transformer.storageClasses)
    }
}
