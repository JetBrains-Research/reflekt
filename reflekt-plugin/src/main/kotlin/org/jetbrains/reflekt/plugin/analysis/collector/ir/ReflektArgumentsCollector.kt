package org.jetbrains.reflekt.plugin.analysis.collector.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrReflektQueriesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

/**
 * Visits all [IrCall]s to extract all Reflekt queries arguments in the [IrFile].
 */
class ReflektArgumentsCollector(
    private val irReflektQueriesAnalyzer: IrReflektQueriesAnalyzer,
    private val file: IrFile,
    messageCollector: MessageCollector? = null,
) : BaseCollector(irReflektQueriesAnalyzer, messageCollector) {
    override fun visitCall(expression: IrCall) {
        irReflektQueriesAnalyzer.process(expression, file)
        super.visitCall(expression)
    }
}

/**
 *  A compiler plugin extension for searching and collection all Reflekt queries arguments,
 *   e.g. annotations, supertypes, or functions' sognatures.
 */
class ReflektArgumentsCollectorExtension(
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    private val irReflektQueriesAnalyzers: MutableList<IrReflektQueriesAnalyzer> = mutableListOf()

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val mockInstances = IrInstances()
        moduleFragment.files.forEach {
            val analyzer = IrReflektQueriesAnalyzer(mockInstances, pluginContext)
            irReflektQueriesAnalyzers.add(analyzer)
            it.acceptChildrenVoid(ReflektArgumentsCollector(analyzer, it, messageCollector))
        }
    }

    /**
     * Creates [LibraryArguments] from [irReflektQueriesAnalyzers]
     *
     * @return [LibraryArguments]
     */
    fun getArguments(): LibraryArguments {
        var arguments = LibraryArguments()
        irReflektQueriesAnalyzers.forEach {
            arguments = arguments.merge(LibraryArguments.fromIrReflektQueriesAnalyzer(it))
        }
        return arguments
    }
}
