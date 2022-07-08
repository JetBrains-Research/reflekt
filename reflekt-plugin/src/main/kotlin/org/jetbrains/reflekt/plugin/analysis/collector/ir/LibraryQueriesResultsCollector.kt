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
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.utils.Util.log

open class IlibraryQueriesResultsCollectorBase(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val messageCollector: MessageCollector? = null,
) {
    inline fun <K : ReflektQueryArguments, reified V> TypeLibraryQueriesResults<K, V>.filterInstances(
        irReflektQueriesAnalyzer: IrReflektQueriesAnalyzer,
        instancesKind: ReflektEntity,
    ) {
        for ((key, value) in this) {
            value += irReflektQueriesAnalyzer.filterInstancesByArguments(key, instancesKind).mapNotNull { it as? V }
        }
    }

    protected fun generate(pluginContext: IrPluginContext, libraryQueriesResults: LibraryQueriesResults) {
        val irInstances = irInstancesAnalyzer.getIrInstances()
        val analyzer = IrReflektQueriesAnalyzer(irInstances, pluginContext)
        messageCollector?.log("Start filtering instances for the ReflektImpl file")
        libraryQueriesResults.classes.filterInstances(analyzer, ReflektEntity.CLASSES)
        libraryQueriesResults.objects.filterInstances(analyzer, ReflektEntity.OBJECTS)
        libraryQueriesResults.functions.filterInstances(analyzer, ReflektEntity.FUNCTIONS)
        messageCollector?.log("Finish filtering instances for the ReflektImpl file")
    }
}

class LibraryQueriesResultsCollector(
    irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryQueriesResults: LibraryQueriesResults,
    messageCollector: MessageCollector? = null,
) : IrGenerationExtension, ILibraryQueriesResultsCollectorBase(irInstancesAnalyzer, messageCollector) {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        generate(pluginContext, libraryQueriesResults)
    }
}

// We should call LibraryQueriesResults#fromLibraryArguments in tests to get the results
// TODO: can we avoid creating this extension?
class LibraryQueriesResultsCollectorForTests(
    irInstancesAnalyzer: IrInstancesAnalyzer,
    private val libraryArguments: LibraryArguments,
    private val libraryQueriesResults: LibraryQueriesResults,
    messageCollector: MessageCollector? = null,
) : IrGenerationExtension, ILibraryQueriesResultsCollectorBase(irInstancesAnalyzer, messageCollector) {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        libraryQueriesResults.merge(LibraryQueriesResults.fromLibraryArguments(libraryArguments))
        generate(pluginContext, libraryQueriesResults)
    }
}
