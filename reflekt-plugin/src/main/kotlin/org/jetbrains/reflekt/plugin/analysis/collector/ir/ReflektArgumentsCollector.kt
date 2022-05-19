package org.jetbrains.reflekt.plugin.analysis.collector.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.IrInstancesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrReflektQueriesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments
import org.jetbrains.reflekt.plugin.analysis.models.psi.SignatureToAnnotations
import org.jetbrains.reflekt.plugin.analysis.models.psi.SupertypesToAnnotations
import org.jetbrains.reflekt.plugin.analysis.processor.fullName
import org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments.getReflektInvokeParts
import org.jetbrains.reflekt.plugin.generation.common.ReflektInvokeParts
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

/**
 * Visits all [IrCall]s to extract all Reflekt queries arguments in the [IrFile].
 */
class ReflektArgumentsCollector(
    pluginContext: IrPluginContext,
    irInstances: IrInstances,
    private val reflektQueriesArguments: LibraryArguments,
    private val messageCollector: MessageCollector? = null,
) : IrElementTransformerVoidWithContext() {
    private val analyzer = IrReflektQueriesAnalyzer(irInstances, pluginContext)

    override fun visitCall(expression: IrCall): IrExpression {
        val invokeParts = expression.getReflektInvokeParts() ?: return super.visitCall(expression)
        messageCollector?.log("[IR] INVOKE PARTS: $invokeParts")
        expression.collectReflektQueriesArgument(invokeParts)
        return super.visitCall(expression)
    }

    private fun IrCall.collectReflektQueriesArgument(invokeParts: ReflektInvokeParts) {
        analyzer.parseReflektQueriesArguments(this)?.let { args ->
            when (invokeParts.entityType) {
                ReflektEntity.OBJECTS -> reflektQueriesArguments.objects.getOrPut(currentFile.fullName) { mutableSetOf() }.add(args as SupertypesToAnnotations)
                ReflektEntity.CLASSES -> reflektQueriesArguments.classes.getOrPut(currentFile.fullName) { mutableSetOf() }.add(args as SupertypesToAnnotations)
                ReflektEntity.FUNCTIONS -> reflektQueriesArguments.functions.getOrPut(currentFile.fullName) { mutableSetOf() }
                    .add(args as SignatureToAnnotations)
            }
        }
    }
}

/**
 *  A compiler plugin extension for searching and collection all Reflekt queries arguments,
 *   e.g. annotations, supertypes, or functions' sognatures.
 */
class ReflektArgumentsCollectorExtension(
    private val irInstancesAnalyzer: IrInstancesAnalyzer,
    private val reflektQueriesArguments: LibraryArguments,
    private val messageCollector: MessageCollector? = null,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irInstances = irInstancesAnalyzer.getIrInstances()
        moduleFragment.transform(ReflektArgumentsCollector(pluginContext, irInstances, reflektQueriesArguments, messageCollector), null)
    }
}
