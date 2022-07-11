package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances

abstract class BaseReflektIrGenerationExtension(private val irInstancesAnalyzer: IrInstancesAnalyzer) : IrGenerationExtension {
    abstract fun getTransformer(
        pluginContext: IrPluginContext,
        irInstances: IrInstances,
        storageClassGenerator: StorageClassGenerator,
    ): BaseReflektIrTransformer

    /**
     * Replaces IR in the Reflekt queries to the list of the found entities.
     */
    final override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irInstances = irInstancesAnalyzer.getIrInstances()
        if (irInstances.isEmpty()) {
            return
        }
        val storageClassGenerator = StorageClassGenerator(pluginContext)
        val transformer = getTransformer(pluginContext, irInstances, storageClassGenerator)
        moduleFragment.transform(transformer, null)
        transformer.storageClasses.values.forEach { (storageClass, storedClasses) ->
            storageClassGenerator.contributeInitializer(storageClass, storedClasses)
        }
    }
}
