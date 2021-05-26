package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.ir.ReflektFunctionInvokeArgumentsCollector
import io.reflekt.plugin.analysis.ir.ReflektInvokeArgumentsCollector
import io.reflekt.plugin.analysis.models.IrReflektUses
import io.reflekt.plugin.generation.common.ReflektInvokeParts
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization

class ReflektIrTransformer(
    private val messageCollector: MessageCollector?,
    private val pluginContext: IrPluginContext,
    private val uses: IrReflektUses
) : BaseReflektIrTransformer() {
    override fun visitCall(expression: IrCall): IrExpression {
        val function = expression.symbol.owner
        val expressionFqName = function.fqNameForIrSerialization.toString()
        val invokeParts = ReflektInvokeParts.parse(expressionFqName) ?: return super.visitCall(expression)
        messageCollector?.log("[IR] REFLEKT CALL: $expressionFqName;")

        val builder = object : IrBuilderWithScope(pluginContext, currentScope!!.scope, UNDEFINED_OFFSET, UNDEFINED_OFFSET) {}

        val call = when (invokeParts.entityType) {
            ReflektEntity.OBJECTS, ReflektEntity.CLASSES -> {
                val invokeArguments = ReflektInvokeArgumentsCollector.collectInvokeArguments(expression)
                val usesType = if (invokeParts.entityType == ReflektEntity.OBJECTS) uses.objects else uses.classes
                builder.resultIrCall(
                    invokeParts,
                    usesType[invokeArguments]!!,
                    expression.type,
                    pluginContext
                )
            }
            ReflektEntity.FUNCTIONS -> {
                val invokeArguments = ReflektFunctionInvokeArgumentsCollector.collectInvokeArguments(expression)
                val usesType = uses.functions
                builder.functionResultIrCall(
                    invokeParts,
                    usesType[invokeArguments]!!,
                    expression.type,
                    pluginContext
                )
            }
        }
        messageCollector?.log("GENERATE IR CALL:\n${call.dump()}")
        return call
    }
}
