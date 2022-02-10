package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

import java.io.File

/**
 * Kotlin compiler extension to replace IR in the SmartReflekt queries
 *
 * @param irInstances
 * @param classpath project dependencies that can be resolved at the compile time
 * @param messageCollector
 */
class SmartReflektIrGenerationExtension(
    private val irInstances: IrInstances,
    private val classpath: List<File>,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    /**
     * Replace IR in the SmartReflekt queries to the list of the found entities
     */
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (irInstances.isEmpty()) {
            return
        }
        moduleFragment.transform(SmartReflektIrTransformer(irInstances, pluginContext, classpath, messageCollector), null)
    }
}
