package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.common.*
import io.reflekt.plugin.analysis.ir.*
import io.reflekt.plugin.analysis.models.IrFunctionInfo
import io.reflekt.plugin.generation.common.*
import io.reflekt.plugin.generation.ir.util.*
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.FqName

/* Base class for Reflekt IR transformers */
open class BaseReflektIrTransformer(private val messageCollector: MessageCollector?) : IrElementTransformerVoidWithContext() {
    /*
     * Constructs replacement for result of Reflekt terminal function (toList/toSet/etc) for classes or objects
     * @param invokeParts info about invoke call to retrieve entity type [objects/classes] and terminal function [toList/toSet/etc]]
     * @param resultValues list of qualified names of objects or classes to return
     */
    protected fun IrBuilderWithScope.resultIrCall(
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<String>,
        resultType: IrType,
        context: IrPluginContext
    ): IrExpression {
        require(resultType is IrSimpleType)
        val itemType = resultType.arguments[0].typeOrNull
            ?: throw ReflektGenerationException("Return type must have at one type argument (e. g. List<T>, Set<T>)")

        val items = resultValues.map {
            context.referenceClass(FqName(it)) ?: throw ReflektGenerationException("Failed to find class $it")
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

    /*
     * Constructs replacement for result of Reflekt terminal function (toList/toSet/etc) for functions
     * @param invokeParts info about invoke call terminal function [toList/toSet/etc]]
     * @param resultValues list of function qualified names with additional info to generate right call
     */
    protected fun IrBuilderWithScope.functionResultIrCall(
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<IrFunctionInfo>,
        resultType: IrType,
        context: IrPluginContext
    ): IrExpression {
        require(resultType is IrSimpleType)
        val itemType = resultType.arguments[0].typeOrNull
            ?: throw ReflektGenerationException("Return type must have at one type argument (e. g. List<T>, Set<T>)")
        require(itemType is IrSimpleType)

        messageCollector?.log("RES ARGS: ${itemType.arguments.map { (it as IrSimpleType).classFqName }}")
        val items = resultValues.map {
            val functionSymbol = context.referenceFunctions(FqName(it.fqName)).firstOrNull { symbol ->
                symbol.owner.getSignature().matchInto(itemType.toParameterizedType())
            } ?: throw ReflektGenerationException("Failed to find function ${it.fqName} with signature ${itemType.toParameterizedType()}")
            irKFunction(itemType, functionSymbol).also { call ->
                if (it.receiverFqName != null && it.isObjectReceiver) {
                    val dispatchSymbol = context.referenceClass(FqName(it.receiverFqName))
                        ?: throw ReflektGenerationException("Failed to find receiver class ${it.receiverFqName}")
                    call.dispatchReceiver = irGetObject(dispatchSymbol)
                }
            }
        }
        return irCall(invokeParts.irTerminalFunction(context), type = resultType).also { call ->
            call.putTypeArgument(0, itemType)
            call.putValueArgument(0, irVarargOut(itemType, items))
        }
    }

    protected fun newIrBuilder(pluginContext: IrPluginContext) =
        object : IrBuilderWithScope(pluginContext, currentScope!!.scope, UNDEFINED_OFFSET, UNDEFINED_OFFSET) {}
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
