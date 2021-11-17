package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.reflekt.plugin.analysis.models.ReflektContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class ReflektIrGenerationExtension(
    private val reflektContext: ReflektContext,
    private val toReplaceIr: Boolean,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (!toReplaceIr) {
            return
        }
        val uses = reflektContext.uses ?: return
        moduleFragment.transform(ReflektIrTransformer(pluginContext, uses, messageCollector), null)
    }
}
