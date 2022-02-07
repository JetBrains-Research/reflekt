@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrReflektContext

/**
 * Replaces Reflekt invoke calls with their results
 *
 * @param reflektContext [IrReflektContext] to extract Reflekt uses
 * @param toReplaceIr if should enable this extension
 * @param messageCollector
 */
class ReflektIrGenerationExtension(
    private val reflektContext: IrReflektContext,
    private val toReplaceIr: Boolean,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    /**
     * Replace IR in the Reflekt queries to the list of the found entities
     */
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (!toReplaceIr) {
            return
        }
        val uses = reflektContext.uses ?: return
        moduleFragment.transform(ReflektIrTransformer(pluginContext, uses, messageCollector), null)
    }
}
