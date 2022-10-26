package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.reflekt.plugin.analysis.analyzer.IrReflektQueriesAnalyzer
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.ir.toFunctionInfo
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrInstances
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryArguments
import org.jetbrains.reflekt.plugin.analysis.processor.ir.reflektArguments.getReflektInvokeParts
import org.jetbrains.reflekt.plugin.utils.Util.log

/**
 * Replaces Reflekt invoke calls with their results.
 *
 * @property pluginContext
 * @property irInstances
 * @property messageCollector
 * @property analyzer [IrReflektQueriesAnalyzer] to extract Reflekt queries arguments,
 *  e.g. supertypes, annotations, functions signatures
 * @property libraryArguments TODO: use for ReflektImpl generation
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY", "UnusedPrivateMember")
class ReflektIrTransformer(
    pluginContext: IrPluginContext,
    private val irInstances: IrInstances,
    private val libraryArguments: LibraryArguments,
    private val messageCollector: MessageCollector? = null,
    storageClassGenerator: StorageClassGenerator = StorageClassGenerator(pluginContext),
) : BaseReflektIrTransformer(pluginContext, messageCollector, storageClassGenerator) {
    private val analyzer = IrReflektQueriesAnalyzer(irInstances, pluginContext)

    /**
     * Visit [IrCall] and replace IR to found entities if it is a Reflekt query
     *
     * @param expression [IrCall]
     */
    @Suppress("ReturnCount")
    override fun visitCall(expression: IrCall): IrExpression {
        messageCollector?.log("[IR] CURRENT EXPRESSION:\n${expression.dump()}")
        val filteredInstances = expression.filterInstances()
        messageCollector?.log("[IR] FILTERED INSTANCES: $filteredInstances")
        val invokeParts = expression.getReflektInvokeParts() ?: return super.visitCall(expression)
        messageCollector?.log("[IR] INVOKE PARTS: $invokeParts")

        // TODO: delete duplicate with SmartReflektIrTransformer
        val call = when (invokeParts.entityType) {
            ReflektEntity.OBJECTS, ReflektEntity.CLASSES -> resultIrCall(
                currentFile.module,
                invokeParts,
                filteredInstances.mapNotNull { (it as? IrClass)?.fqNameWhenAvailable?.asString() },
                expression.type,
            )
            ReflektEntity.FUNCTIONS -> functionResultIrCall(
                invokeParts,
                filteredInstances.mapNotNull { (it as? IrFunction)?.toFunctionInfo() },
                expression.type,
            )
        }

        messageCollector?.log("[IR] FOUND CALL (${invokeParts.entityType}):\n${expression.dump()}")
        messageCollector?.log("[IR] GENERATE CALL:\n${call.dump()}")
        return call
    }

    /**
     * Filters instances according to [IrCall].
     * If [irInstances] is empty we don't need to process [IrCall].
     * If [IrCall] is a Reflekt call then extract all query arguments and filter instances.
     */
    private fun IrCall.filterInstances() = if (irInstances.isEmpty()) {
        emptyList()
    } else {
        analyzer.processWithCurrentResult(this, currentFile)
    }
}
