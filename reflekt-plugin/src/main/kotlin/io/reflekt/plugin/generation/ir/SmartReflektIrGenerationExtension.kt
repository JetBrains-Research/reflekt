package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.models.ReflektContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import java.io.File

class SmartReflektIrGenerationExtension(
    private val classpath: List<File>,
    private val reflektContext: ReflektContext,
    private val toReplaceIr: Boolean,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        if (toReplaceIr) return
        val instances = reflektContext.instances ?: error("Instances must be saved to reflektContext before running SmartReflektIrGenerationExtension")
        moduleFragment.transform(SmartReflektIrTransformer(pluginContext, instances, classpath, messageCollector), null)
    }
}
