package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.reflekt.plugin.analysis.common.*
import org.jetbrains.reflekt.plugin.analysis.ir.isSubtypeOf
import org.jetbrains.reflekt.plugin.analysis.ir.toParameterizedType
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrFunctionInfo
import org.jetbrains.reflekt.plugin.generation.common.*
import org.jetbrains.reflekt.plugin.generation.ir.util.*
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.FqName

/**
 * Generate IR representation for the Reflekt terminal function (toList/toSet/etc)
 */
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

/**
 * Base class for Reflekt IR transformers
 *
 * @property messageCollector
 */
open class BaseReflektIrTransformer(private val messageCollector: MessageCollector?) : IrElementTransformerVoidWithContext() {
    /**
     * Constructs replacement for result of Reflekt terminal function (toList/toSet/etc) for classes or objects
     *
     * @param invokeParts info about invoke call to retrieve entity type (objects/classes) and terminal function (toList/toSet/etc)
     * @param resultValues list of qualified names of objects or classes to return
     * @param resultType
     * @param context
     * @return replacement for a result of terminal function
     * @throws ReflektGenerationException
     */
    protected fun IrBuilderWithScope.resultIrCall(
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<String>,
        resultType: IrType,
        context: IrPluginContext,
    ): IrExpression {
        require(resultType is IrSimpleType)

        val itemType = resultType.arguments[0].typeOrNull
            ?: throw ReflektGenerationException("Return type must have at one type argument (e. g. List<T>, Set<T>)")

        val items = resultValues.map {
            context.referenceClass(FqName(it)) ?: throw ReflektGenerationException("Failed to find class $it")
        }.map {
            when (invokeParts.entityType) {
                ReflektEntity.OBJECTS -> irGetObject(it)
                ReflektEntity.CLASSES -> itemType.castTo(irKClass(it))
                ReflektEntity.FUNCTIONS -> error("Use functionResultIrCall")
            }
        }
        return irCall(invokeParts.irTerminalFunction(context), type = resultType).also { call ->
            call.putTypeArgument(0, itemType)
            call.putValueArgument(0, irVarargOut(itemType, items))
        }
    }

    /**
     * Constructs replacement for result of Reflekt terminal function (toList/toSet/etc) for functions
     *
     * @param invokeParts info about invoke call terminal function (toList/toSet/etc)
     * @param resultValues list of function qualified names with additional info to generate the right call
     * @param resultType
     * @param context
     * @return [IrExpression]
     * @throws ReflektGenerationException
     */
    @ObsoleteDescriptorBasedAPI
    @Suppress("TOO_MANY_LINES_IN_LAMBDA", "ThrowsCount")
    protected fun IrBuilderWithScope.functionResultIrCall(
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<IrFunctionInfo>,
        resultType: IrType,
        context: IrPluginContext,
    ): IrExpression {
        require(resultType is IrSimpleType)
        val itemType = resultType.arguments[0].typeOrNull
            ?: throw ReflektGenerationException("Return type must have one type argument (e. g. List<T>, Set<T>)")
        require(itemType is IrSimpleType)

        messageCollector?.log("RES ARGS: ${itemType.arguments.map { (it as IrSimpleType).classFqName }}")
        messageCollector?.log("size of result values ${resultValues.size}")
        val items = resultValues.map { irFunctionInfo ->
            val functionSymbol = context.referenceFunctions(FqName(irFunctionInfo.fqName)).firstOrNull { symbol ->
                symbol.owner.isSubtypeOf(itemType, context).also { messageCollector?.log("${symbol.owner.isSubtypeOf(itemType, context)}") }
            }
            messageCollector?.log("function symbol is $functionSymbol")
            functionSymbol ?: run {
                messageCollector?.log("function symbol is null")
                throw ReflektGenerationException("Failed to find function ${irFunctionInfo.fqName} with signature ${itemType.toParameterizedType()}")
            }
            irKFunction(itemType, functionSymbol).also { call ->
                irFunctionInfo.receiverFqName?.let {
                    if (irFunctionInfo.isObjectReceiver) {
                        val dispatchSymbol = context.referenceClass(FqName(irFunctionInfo.receiverFqName))
                            ?: throw ReflektGenerationException("Failed to find receiver class ${irFunctionInfo.receiverFqName}")
                        call.dispatchReceiver = irGetObject(dispatchSymbol)
                    }
                }
            }
        }
        return irCall(invokeParts.irTerminalFunction(context), type = resultType).also { call ->
            call.putTypeArgument(0, itemType)
            call.putValueArgument(0, irVarargOut(itemType, items))
        }
    }

    /**
     * Create a new [IrBuilderWithScope] to generate IR
     *
     * @param pluginContext
     */
    protected fun newIrBuilder(pluginContext: IrPluginContext) =
        object : IrBuilderWithScope(
            pluginContext,
            currentScope!!.scope,
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET,
        ) {
            // no need to pass a body to this object
        }
}
