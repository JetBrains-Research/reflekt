@file:Suppress("FILE_WILDCARD_IMPORTS", "KDOC_WITHOUT_RETURN_TAG")

package org.jetbrains.reflekt.plugin.generation.ir.util

import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.types.Variance

/**
 * Generates IR for varargs based on the list of [elements].
 *
 * @param elementType type of varargs in [elements] (consider <out> projection)
 * @param elements
 */
fun IrBuilderWithScope.irVarargOut(elementType: IrType, elements: List<IrExpression>): IrVararg = IrVarargImpl(
    startOffset = UNDEFINED_OFFSET,
    endOffset = UNDEFINED_OFFSET,
    type = context.irBuiltIns.arrayClass.typeWithArguments(listOf(makeTypeProjection(elementType, Variance.OUT_VARIANCE))),
    varargElementType = elementType,
    elements = elements,
)

/**
 * Generates IR for [kotlin.reflect.KClass] reference for the given type.
 *
 * @param symbol the symbol of the class to be referenced.
 */
fun IrBuilderWithScope.irClassReference(symbol: IrClassSymbol): IrClassReference = IrClassReferenceImpl(
    UNDEFINED_OFFSET,
    UNDEFINED_OFFSET,
    context.irBuiltIns.kClassClass.typeWith(symbol.defaultType),
    symbol,
    symbol.defaultType,
)

/**
 * Generates IR for FunctionN.
 *
 * @param type function types (e.g. return type, arguments type, etc)
 * @param symbol [IrFunctionSymbol]
 */
@Suppress("FUNCTION_NAME_INCORRECT_CASE")
fun IrBuilderWithScope.irKFunction(type: IrType, symbol: IrFunctionSymbol): IrFunctionReference {
    require(type is IrSimpleType)
    val kFunctionType = IrSimpleTypeImpl(
        context.irBuiltIns.kFunctionN(type.arguments.size - 1).symbol,
        false,
        type.arguments,
        emptyList(),
    )
    // Todo: are we sure there should be 0 typeArgumentsCount?
    return IrFunctionReferenceImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = kFunctionType,
        symbol = symbol,
        typeArgumentsCount = 0,
        valueArgumentsCount = type.arguments.size,
    )
}

/**
 * Casts [type] to the [castTo] type, e.g. to KClass.
 *
 * @param type
 * @param castTo [IrExpression]
 */
fun irTypeCast(type: IrType, castTo: IrExpression) = IrTypeOperatorCallImpl(
    startOffset = UNDEFINED_OFFSET,
    endOffset = UNDEFINED_OFFSET,
    type = type,
    operator = IrTypeOperator.CAST,
    typeOperand = type,
    argument = this,
)
