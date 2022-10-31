@file:Suppress("FILE_WILDCARD_IMPORTS", "KDOC_WITHOUT_RETURN_TAG")

package org.jetbrains.reflekt.plugin.generation.ir.util

import org.jetbrains.kotlin.ir.builders.IrBuilder
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance

/**
 * Generates IR for varargs based on the list of [elements].
 *
 * @param elementType type of varargs in [elements] (consider <out> projection)
 * @param elements
 */
fun IrBuilder.irVarargOut(elementType: IrType, elements: List<IrExpression>) = IrVarargImpl(
    startOffset = startOffset,
    endOffset = endOffset,
    type = context.irBuiltIns.arrayClass.typeWithArguments(listOf(makeTypeProjection(elementType, Variance.OUT_VARIANCE))),
    varargElementType = elementType,
    elements = elements,
)

fun IrBuilder.irGetEnumValue(type: IrType, symbol: IrEnumEntrySymbol) = IrGetEnumValueImpl(startOffset, endOffset, type, symbol)

fun <T> IrBuilder.irVariableVal(
    parent: T,
    name: Name,
    type: IrType,
    isConst: Boolean,
    isLateinit: Boolean,
) where T : IrDeclaration, T : IrDeclarationParent = IrVariableImpl(
    startOffset, endOffset, parent.origin,
    IrVariableSymbolImpl(),
    name,
    type,
    false,
    isConst,
    isLateinit,
).also {
    it.parent = parent
}

/**
 * Generates IR for [kotlin.reflect.KClass] reference for the given type.
 *
 * @param symbol the symbol of the class to be referenced.
 */
fun IrBuilder.irClassReference(symbol: IrClassSymbol) = IrClassReferenceImpl(
    startOffset,
    endOffset,
    context.irBuiltIns.kClassClass.typeWith(symbol.defaultType),
    symbol,
    symbol.defaultType,
)

/**
 * Generates IR of function reference.
 *
 * @param type function types (e.g. return type, arguments type, etc)
 * @param symbol [IrFunctionSymbol]
 * @return a new function reference.
 */
fun IrBuilder.irFunctionReference(type: IrSimpleType, symbol: IrFunctionSymbol): IrFunctionReferenceImpl {
    val kFunctionType = IrSimpleTypeImpl(
        context.irBuiltIns.kFunctionN(type.arguments.size - 1).symbol,
        false,
        type.arguments,
        emptyList(),
    )
    // Todo: are we sure there should be 0 typeArgumentsCount?
    return IrFunctionReferenceImpl(
        startOffset = startOffset,
        endOffset = endOffset,
        type = kFunctionType,
        symbol = symbol,
        typeArgumentsCount = 0,
        valueArgumentsCount = type.arguments.size,
    )
}

/**
 * Casts [type] to the [castTo] type, e.g., to KClass.
 *
 * @param type
 * @param castTo [IrExpression]
 */
fun IrBuilder.irTypeCast(type: IrType, castTo: IrExpression) = IrTypeOperatorCallImpl(
    startOffset = startOffset,
    endOffset = endOffset,
    type = type,
    operator = IrTypeOperator.CAST,
    typeOperand = type,
    argument = castTo,
)

fun IrBuilderWithScope.irCall(
    callee: IrFunctionSymbol,
    typeArguments: List<IrType?> = emptyList(),
    dispatchReceiver: IrExpression? = null,
    extensionReceiver: IrExpression? = null,
    valueArguments: List<IrExpression?> = emptyList(),
) = irCall(callee, callee.owner.returnType).also { call ->
    typeArguments.forEachIndexed { index, argument ->
        call.putTypeArgument(index, argument)
    }
    call.dispatchReceiver = dispatchReceiver
    call.extensionReceiver = extensionReceiver
    valueArguments.forEachIndexed { index, argument ->
        call.putValueArgument(index, argument)
    }
}
