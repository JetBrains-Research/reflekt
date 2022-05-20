package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

import java.io.File

/**
 * The Kotlin compiler extension to replace IR in the SmartReflekt queries.
 *
 * @param irInstancesAnalyzer to get project instances
 * @param classpath project dependencies that can be resolved at the compile time
 * @param messageCollector
 */
class SmartReflektIrGenerationExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val classpath: List<File>,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    /**
     * Replaces IR in the SmartReflekt queries to the list of the found entities.
     */
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irInstances = irInstancesAnalyzer.getIrInstances()
        if (irInstances.isEmpty()) {
            return
        }
        moduleFragment.transform(SmartReflektIrTransformer(irInstances, pluginContext, classpath, messageCollector), null)
    }
}
