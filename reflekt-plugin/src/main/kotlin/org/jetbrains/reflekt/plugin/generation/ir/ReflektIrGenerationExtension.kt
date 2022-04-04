@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArgumentsWithInstances

/**
 * Replaces Reflekt invoke calls with their results
 *
 * @param toReplaceIr if should enable this extension
 * @param irInstancesAnalyzer to get project instances
 * @param messageCollector
 */
class ReflektIrGenerationExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryArguments: LibraryArguments,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    /**
     * Replace IR in the Reflekt queries to the list of the found entities
     */
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irInstances = irInstancesAnalyzer.getIrInstances()
        if (irInstances.isEmpty()) {
            return
        }
        moduleFragment.transform(ReflektIrTransformer(pluginContext, irInstances, libraryArguments, messageCollector), null)
    }
}
