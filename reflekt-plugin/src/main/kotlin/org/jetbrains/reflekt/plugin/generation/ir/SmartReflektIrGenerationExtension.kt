package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import java.io.File

/**
 * The Kotlin compiler extension to replace IR in the SmartReflekt queries.
 *
 * @param irInstancesAnalyzer to get project instances
 * @param classpath project dependencies that can be resolved at the compile-time
 * @param messageCollector
 */
class SmartReflektIrGenerationExtension(
    irInstancesAnalyzer: IrInstancesAnalyzer,
    private val classpath: List<File>,
    private val messageCollector: MessageCollector? = null,
) : BaseReflektIrGenerationExtension(irInstancesAnalyzer) {
    override fun getTransformer(
        pluginContext: IrPluginContext,
        irInstances: IrInstances,
        storageClassGenerator: StorageClassGenerator
    ): BaseReflektIrTransformer = SmartReflektIrTransformer(irInstances, pluginContext, classpath, messageCollector, storageClassGenerator)
}
