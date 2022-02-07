@file:Suppress("FILE_WILDCARD_IMPORTS", "KDOC_WITHOUT_RETURN_TAG")

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

/**
 * Generate IR representation for varargs based on the list of [elements]
 *
 * @param elementType type of varargs in [elements] (consider <out> projection)
 * @param elements
 */
fun IrBuilderWithScope.irVarargOut(elementType: IrType, elements: List<IrExpression>) =
    IrVarargImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = context.irBuiltIns.arrayClass.typeWithArguments(listOf(makeTypeProjection(elementType, Variance.OUT_VARIANCE))),
        varargElementType = elementType,
        elements = elements,
    )

/**
 * Generate IR representation for KClass
 *
 * @param symbol [IrFunctionSymbol]
 */
@Suppress("FUNCTION_NAME_INCORRECT_CASE")
fun IrBuilderWithScope.irKClass(symbol: IrClassSymbol) =
    IrClassReferenceImpl(
        UNDEFINED_OFFSET,
        UNDEFINED_OFFSET,
        context.irBuiltIns.kClassClass.typeWith(symbol.defaultType),
        symbol,
        symbol.defaultType,
    )

/**
 * Generate IR representation for FunctionN
 *
 * @param type function types (e.g. return type, arguments type, etc)
 * @param symbol [IrFunctionSymbol]
 */
@Suppress("FUNCTION_NAME_INCORRECT_CASE")
fun IrBuilderWithScope.irKFunction(type: IrType, symbol: IrFunctionSymbol): IrFunctionReference {
    require(type is IrSimpleType)
    val functionFactory = context.irBuiltIns.functionFactory
    val kFunctionType = IrSimpleTypeImpl(
        // TODO: replace to this one in the next Kotlin version since API will be changed
        // context.irBuiltIns.functionN(type.arguments.size - 1).symbol,
        functionFactory.kFunctionN(type.arguments.size - 1).symbol,
        false,
        type.arguments,
        emptyList(),
    )
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
 * Cast [type] to the [castTo] type, e.g. to KClass
 *
 * @param type
 * @param castTo [IrExpression]
 */
fun irTypeCast(type: IrType, castTo: IrExpression) =
    IrTypeOperatorCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = type,
        operator = IrTypeOperator.CAST,
        typeOperand = type,
        argument = castTo,
    )

/**
 * Generate IR representation for <collection_name>Of function, e.g. listOf or setOf
 *
 * @param collectionFqName e.g. kotlin.collections.listOf
 * @param pluginContext
 */
fun funCollectionOf(collectionFqName: String, pluginContext: IrPluginContext) =
    pluginContext.referenceFunctions(FqName(collectionFqName))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].isVararg
        }

/**
 * Generate IR representation for listOf function
 *
 * @param pluginContext
 */
fun funListOf(pluginContext: IrPluginContext) = funCollectionOf("kotlin.collections.listOf", pluginContext)

/**
 * Generate IR representation for setOf function
 *
 * @param pluginContext
 */
fun funSetOf(pluginContext: IrPluginContext) = funCollectionOf("kotlin.collections.setOf", pluginContext)
