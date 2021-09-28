package org.jetbrains.reflekt.plugin.generation.ir.util

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.Variance

fun IrBuilderWithScope.irVarargOut(elementType: IrType, elements: List<IrExpression>) =
    IrVarargImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = context.irBuiltIns.arrayClass.typeWithArguments(listOf(makeTypeProjection(elementType, Variance.OUT_VARIANCE))),
        varargElementType = elementType,
        elements = elements
    )

fun irTypeCast(type: IrType, argument: IrExpression) =
    IrTypeOperatorCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = type,
        operator = IrTypeOperator.CAST,
        typeOperand = type,
        argument = argument
    )

fun IrBuilderWithScope.irKClass(symbol: IrClassSymbol) =
    IrClassReferenceImpl(
        UNDEFINED_OFFSET,
        UNDEFINED_OFFSET,
        context.irBuiltIns.kClassClass.typeWith(symbol.defaultType),
        symbol,
        symbol.defaultType
    )

fun IrBuilderWithScope.irKFunction(type: IrType, symbol: IrFunctionSymbol): IrFunctionReference {
    require(type is IrSimpleType)
    val kFunctionType = IrSimpleTypeImpl(
        // TODO: replace to this one in the next Kotlin version since API will be changed
//        context.irBuiltIns.functionN(type.arguments.size - 1).symbol,
        context.irBuiltIns.functionFactory.kFunctionN(type.arguments.size - 1).symbol,
        false,
        type.arguments,
        emptyList()
    )
    return IrFunctionReferenceImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = kFunctionType,
        symbol = symbol,
        typeArgumentsCount = 0,
        valueArgumentsCount = type.arguments.size
    )
}

fun funListOf(pluginContext: IrPluginContext) =
    pluginContext.referenceFunctions(FqName("kotlin.collections.listOf"))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].isVararg
        }

fun funSetOf(pluginContext: IrPluginContext) =
    pluginContext.referenceFunctions(FqName("kotlin.collections.setOf"))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].isVararg
        }
