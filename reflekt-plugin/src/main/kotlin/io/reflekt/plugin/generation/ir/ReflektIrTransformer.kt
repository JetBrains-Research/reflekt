package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.ir.ReflektFunctionInvokeArgumentsCollector
import io.reflekt.plugin.analysis.ir.ReflektInvokeArgumentsCollector
import io.reflekt.plugin.analysis.models.IrReflektUses
import io.reflekt.plugin.generation.common.ReflektGenerationException
import io.reflekt.plugin.generation.common.ReflektInvokeParts
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization

/* Replaces Reflekt invoke calls with their results */
class ReflektIrTransformer(
    private val pluginContext: IrPluginContext,
    private val uses: IrReflektUses,
    private val messageCollector: MessageCollector? = null
) : BaseReflektIrTransformer(messageCollector) {

    @ObsoleteDescriptorBasedAPI
    override fun visitCall(expression: IrCall): IrExpression {
        val function = expression.symbol.owner
        val expressionFqName = function.fqNameForIrSerialization.toString()
        val invokeParts = ReflektInvokeParts.parse(expressionFqName) ?: return super.visitCall(expression)
        messageCollector?.log("[IR] REFLEKT CALL: $expressionFqName;")

        val call = when (invokeParts.entityType) {
            ReflektEntity.OBJECTS, ReflektEntity.CLASSES -> {
                val invokeArguments = ReflektInvokeArgumentsCollector.collectInvokeArguments(expression)
                val usesType = if (invokeParts.entityType == ReflektEntity.OBJECTS) uses.objects else uses.classes
                messageCollector?.log("[IR] INVOKE ARGUMENTS: $invokeArguments")
                newIrBuilder(pluginContext).resultIrCall(
                    invokeParts,
                    usesType[invokeArguments] ?: throw ReflektGenerationException("No uses stored for $invokeArguments"),
                    expression.type,
                    pluginContext
                )
            }
            ReflektEntity.FUNCTIONS -> {
                val invokeArguments = ReflektFunctionInvokeArgumentsCollector.collectInvokeArguments(expression)
                val usesType = uses.functions
                messageCollector?.log("[IR] INVOKE ARGUMENTS: $invokeArguments")
                newIrBuilder(pluginContext).functionResultIrCall(
                    invokeParts,
                    usesType[invokeArguments] ?: throw ReflektGenerationException("No uses stored for $invokeArguments"),
                    expression.type,
                    pluginContext
                )
            }
        }
        messageCollector?.log("[IR] FOUND CALL (${invokeParts.entityType}):\n${expression.dump()}")
        messageCollector?.log("[IR] GENERATE CALL:\n${call.dump()}")
        return call
    }
}
