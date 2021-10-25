package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.models.ReflektContext
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import kotlin.system.measureTimeMillis

class ReflektIrGenerationExtension(
    private val reflektContext: ReflektContext,
    private val toReplaceIr: Boolean,
    private val messageCollector: MessageCollector? = null
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (!toReplaceIr) return
        val uses = reflektContext.uses ?: error("Uses must be saved to reflektContext before running ReflektIrGenerationExtension")
        moduleFragment.transform(ReflektIrTransformer(pluginContext, uses, messageCollector), null)
    }
}
