@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.analysis.collector.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrReflektQueriesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.models.ReflektQueryArguments
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryQueriesResults
import org.jetbrains.reflekt.plugin.analysis.models.ir.TypeLibraryQueriesResults
import org.jetbrains.reflekt.plugin.utils.Util.log

class LibraryQueriesResultsCollector(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryQueriesResults: LibraryQueriesResults,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irInstances = irInstancesAnalyzer.getIrInstances()
        val analyzer = IrReflektQueriesAnalyzer(irInstances, pluginContext)
        messageCollector?.log("Start filtering instances for the ReflektImpl file")
        libraryQueriesResults.classes.filterInstances(analyzer, ReflektEntity.CLASSES)
        libraryQueriesResults.objects.filterInstances(analyzer, ReflektEntity.OBJECTS)
        libraryQueriesResults.functions.filterInstances(analyzer, ReflektEntity.FUNCTIONS)
        messageCollector?.log("Finish filtering instances for the ReflektImpl file")
    }

    private fun <K : ReflektQueryArguments, V> TypeLibraryQueriesResults<K, V>.filterInstances(
        irReflektQueriesAnalyzer: IrReflektQueriesAnalyzer,
        instancesKind: ReflektEntity,
    ) {
        this.forEach { (k, _) ->
            this[k]?.addAll(
                irReflektQueriesAnalyzer.filterInstancesByArguments(k, instancesKind).mapNotNull { it as? V },
            )
        }
    }
}
