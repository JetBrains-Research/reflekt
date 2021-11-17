package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.reflekt.plugin.analysis.models.ir.IrReflektContext

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

import java.io.File

class SmartReflektIrGenerationExtension(
    private val classpath: List<File>,
    private val reflektContext: IrReflektContext,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val instances = reflektContext.instances ?: error("Instances must be saved to reflektContext before running SmartReflektIrGenerationExtension")
        moduleFragment.transform(SmartReflektIrTransformer(pluginContext, instances, classpath, messageCollector), null)
    }
}
