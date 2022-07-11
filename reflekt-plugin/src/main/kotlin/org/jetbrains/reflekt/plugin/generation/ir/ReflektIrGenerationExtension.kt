package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments

/**
 * Replaces Reflekt invoke calls with their results.
 *
 * @property irInstancesAnalyzer to get project instances
 * @property libraryArguments
 * @property messageCollector
 */
class ReflektIrGenerationExtension(
    irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryArguments: LibraryArguments,
    private val messageCollector: MessageCollector? = null,
) : BaseReflektIrGenerationExtension(irInstancesAnalyzer) {
    override fun getTransformer(
        pluginContext: IrPluginContext,
        irInstances: IrInstances,
        storageClassGenerator: StorageClassGenerator
    ): BaseReflektIrTransformer = ReflektIrTransformer(pluginContext, irInstances, libraryArguments, messageCollector, storageClassGenerator)
}
