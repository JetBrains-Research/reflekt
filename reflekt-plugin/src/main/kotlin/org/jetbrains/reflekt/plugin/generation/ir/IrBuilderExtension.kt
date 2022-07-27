package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrAnonymousInitializerSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.reflekt.plugin.analysis.ir.makeTypeProjection
import org.jetbrains.reflekt.plugin.analysis.processor.toReflektVisibility
import org.jetbrains.reflekt.plugin.generation.ir.util.*
import org.jetbrains.reflekt.plugin.generation.ir.util.irCall
import org.jetbrains.reflekt.plugin.utils.getValueArguments

/**
 * Provides utilities for IR generation and transformation: extensions to [IrClass], and to [IrBuilder].
 */
interface IrBuilderExtension {
    val pluginContext: IrPluginContext
    val irBuiltIns: IrBuiltIns
        get() = pluginContext.irBuiltIns
    val generationSymbols: GenerationSymbols

    @OptIn(ObsoleteDescriptorBasedAPI::class)
    fun IrClass.contributeAnonymousInitializer(body: IrBlockBodyBuilder.() -> Unit) {
        factory.createAnonymousInitializer(startOffset, endOffset, origin, IrAnonymousInitializerSymbolImpl(descriptor)).also {
            it.parent = this
            declarations += it
            it.body = DeclarationIrBuilder(pluginContext, it.symbol, startOffset, endOffset).irBlockBody(startOffset, endOffset, body)
        }
    }

    fun IrBuilderWithScope.irCheckNotNull(value: IrExpression) = irCall(
        irBuiltIns.checkNotNullSymbol,
        typeArguments = listOf(value.type.makeNotNull()),
        valueArguments = listOf(value),
    )

    fun IrBuilderWithScope.irMapGet(map: IrExpression, key: IrExpression) =
        irCall(generationSymbols.mapGet, dispatchReceiver = map, valueArguments = listOf(key))

    fun IrBuilderWithScope.irTo(left: IrExpression, right: IrExpression) =
        irCall(generationSymbols.to, typeArguments = listOf(left.type, right.type), extensionReceiver = left, valueArguments = listOf(right))

    fun IrBuilderWithScope.irHashMapOf(keyType: IrType, valueType: IrType, pairs: List<IrExpression>) = irCall(
        generationSymbols.hashMapOf,
        typeArguments = listOf(keyType, valueType),
        valueArguments = listOf(
            irVarargOut(
                generationSymbols.pairClass.createType(
                    false,
                    listOf(keyType.makeTypeProjection(), valueType.makeTypeProjection()),
                ),
                pairs,
            ),
        ),
    )

    fun IrBuilderWithScope.irMutableSetAdd(mutableSet: IrExpression, element: IrExpression) =
        irCall(generationSymbols.mutableSetAdd, dispatchReceiver = mutableSet, valueArguments = listOf(element))

    fun IrBuilderWithScope.irReflektClassImplConstructor(irClassSymbol: IrClassSymbol): IrFunctionAccessExpression {
        val irClass = irClassSymbol.owner
        return irCall(
            generationSymbols.reflektClassImplConstructor,
            typeArguments = listOf(irClassSymbol.defaultType),
            valueArguments = listOf(
                irClassReference(irClassSymbol),
                irCall(
                    generationSymbols.hashSetOf,
                    typeArguments = listOf(irBuiltIns.annotationType),
                    valueArguments = listOf(
                        irVarargOut(irBuiltIns.annotationType,
                            irClass.annotations.map { irCall(it.symbol, valueArguments = it.getValueArguments()) }),
                    ),
                ),
                irBoolean(irClass.modality == Modality.ABSTRACT),
                irBoolean(irClass.isCompanion),
                irBoolean(irClass.isData),
                irBoolean(irClass.modality == Modality.FINAL),
                irBoolean(irClass.isFun),
                irBoolean(irClass.isInner),
                irBoolean(irClass.modality == Modality.OPEN),
                irBoolean(irClass.modality == Modality.SEALED),
                irBoolean(irClass.isValue),
                irString(irClass.kotlinFqName.toString()),
                irCall(generationSymbols.hashSetConstructor),
                irCall(generationSymbols.hashSetConstructor),
                irString(irClass.kotlinFqName.shortName().toString()),
                irGetEnumValue(
                    generationSymbols.reflektVisibilityClass.defaultType,
                    generationSymbols.reflektVisibilityClass
                        .owner
                        .declarations
                        .filterIsInstance<IrEnumEntry>()
                        .first {
                            it.name == Name.identifier(
                                checkNotNull(irClass.visibility.toReflektVisibility()) { "Unsupported visibility of IrClass: ${irClass.visibility}" }.name,
                            )
                        }
                        .symbol,
                ),
                if (irClass.isObject) irGetObject(irClassSymbol) else irNull(),
            ),
        )
    }
}
