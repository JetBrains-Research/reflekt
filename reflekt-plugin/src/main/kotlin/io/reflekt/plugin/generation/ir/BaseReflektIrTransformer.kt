package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.common.*
import io.reflekt.plugin.analysis.models.IrFunctionInfo
import io.reflekt.plugin.generation.common.*
import io.reflekt.plugin.generation.ir.util.*
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.FqName

open class BaseReflektIrTransformer  : IrElementTransformerVoidWithContext() {
    protected fun IrBuilderWithScope.resultIrCall(
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<String>,
        resultType: IrType,
        context: IrPluginContext
    ): IrExpression {
        require(resultType is IrSimpleType)
        val itemType = resultType.arguments[0].typeOrNull!!

        val items = resultValues.map {
            context.referenceClass(FqName(it))!!
        }.map {
            when (invokeParts.entityType) {
                ReflektEntity.OBJECTS -> irGetObject(it)
                ReflektEntity.CLASSES -> irTypeCast(itemType, irKClass(it))
                ReflektEntity.FUNCTIONS -> error("Use functionResultIrCall")
            }
        }
        return irCall(invokeParts.irTerminalFunction(context), type = resultType).also { call ->
            call.putTypeArgument(0, itemType)
            call.putValueArgument(0, irVarargOut(itemType, items))
        }
    }

    protected fun IrBuilderWithScope.functionResultIrCall(
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<IrFunctionInfo>,
        resultType: IrType,
        context: IrPluginContext
    ): IrExpression {
        require(resultType is IrSimpleType)
        val itemType = resultType.arguments[0].typeOrNull!!

        val items = resultValues.map {
            val symbol = context.referenceFunctions(FqName(it.fqName)).single { symbol ->
                val parameters = symbol.owner.valueParameters
                val funTypes = resultType.arguments
                funTypes.size == parameters.size + 1 && parameters.zip(funTypes).all { (parameter, type) ->
                    parameter.type == type
                }
            }
            irKFunction(itemType, symbol).also { call ->
                val receiverFqName = it.dispatchReceiverFqName ?: it.extensionReceiverFqName
                if (receiverFqName != null && it.isObjectReceiver) {
                    val dispatchSymbol = context.referenceClass(FqName(receiverFqName))!!
                    call.dispatchReceiver = irGetObject(dispatchSymbol)
                }
            }
        }
        return irCall(invokeParts.irTerminalFunction(context), type = resultType).also { call ->
            call.putTypeArgument(0, itemType)
            call.putValueArgument(0, irVarargOut(itemType, items))
        }
    }
}

private val BaseReflektInvokeParts.irTerminalFunction: (IrPluginContext) -> IrFunctionSymbol
    get() = when (this) {
        is ReflektInvokeParts -> when (terminalFunction) {
            ReflektTerminalFunction.TO_LIST -> ::funListOf
            ReflektTerminalFunction.TO_SET -> ::funSetOf
        }
        is SmartReflektInvokeParts -> when (terminalFunction) {
            SmartReflektTerminalFunction.RESOLVE -> ::funListOf
        }
    }
