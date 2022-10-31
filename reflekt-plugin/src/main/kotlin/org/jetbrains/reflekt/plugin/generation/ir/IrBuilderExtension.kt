package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrAnonymousInitializerSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.reflekt.plugin.analysis.ir.*
import org.jetbrains.reflekt.plugin.analysis.processor.toReflektVisibility
import org.jetbrains.reflekt.plugin.generation.common.ReflektGenerationException
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

    fun IrBuilderWithScope.irTo(first: IrExpression, second: IrExpression) =
        irCall(generationSymbols.to, typeArguments = listOf(first.type, second.type), extensionReceiver = first, valueArguments = listOf(second))

    fun IrBuilderWithScope.irHashMapOf(keyType: IrType, valueType: IrType, pairs: List<IrExpression>) = irCall(
        generationSymbols.hashMapOf,
        typeArguments = listOf(keyType, valueType),
        valueArguments = listOf(
            /* pairs = */
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
                /* kClass = */ irClassReference(irClassSymbol),
                /* annotations = */
                irCall(
                    generationSymbols.hashSetOf,
                    typeArguments = listOf(irBuiltIns.annotationType),
                    valueArguments = listOf(
                        /* elements = */
                        irVarargOut(irBuiltIns.annotationType,
                            irClass.annotations.map { irCall(it.symbol, valueArguments = it.getValueArguments()) }),
                    ),
                ),
                /* isAbstract = */ irBoolean(irClass.modality == Modality.ABSTRACT),
                /* isCompanion = */ irBoolean(irClass.isCompanion),
                /* isData = */ irBoolean(irClass.isData),
                /* isFinal = */ irBoolean(irClass.modality == Modality.FINAL),
                /* isFun = */ irBoolean(irClass.isFun),
                /* isInner = */ irBoolean(irClass.isInner),
                /* isOpen = */ irBoolean(irClass.modality == Modality.OPEN),
                /* isSealed = */ irBoolean(irClass.modality == Modality.SEALED),
                /* isValue = */ irBoolean(irClass.isValue),
                /* qualifiedName = */ irString(irClass.kotlinFqName.toString()),
                /* superclasses = */ irCall(generationSymbols.hashSetConstructor),
                /* sealedSubclasses = */ irCall(generationSymbols.hashSetConstructor),
                /* simpleName = */ irString(irClass.kotlinFqName.shortName().toString()),
                /* visibility = */
                irGetEnumValue(
                    generationSymbols.reflektVisibilityClass.defaultType,
                    generationSymbols.reflektVisibilityClass.owner.declarations
                        .filterIsInstance<IrEnumEntry>()
                        .first {
                            it.name == Name.identifier(
                                checkNotNull(irClass.visibility.toReflektVisibility()) { "Unsupported visibility of IrClass: ${irClass.visibility}" }.name,
                            )
                        }
                        .symbol,
                ),
                /* objectInstance = */ if (irClass.isObject) irGetObject(irClassSymbol) else irNull(),
            ),
        )
    }

    @OptIn(ObsoleteDescriptorBasedAPI::class)
    fun IrBuilderWithScope.createFunctionReference(
        pluginContext: IrPluginContext,
        irFunction: IrFunction,
        itemType: IrSimpleType,
    ): IrFunctionReference {
        val functionSymbol = pluginContext.referenceFunctions(irFunction.fqNameWhenAvailable!!)
            .find { symbol -> symbol.owner.isSubtypeOf(itemType, pluginContext) }
            ?: throw ReflektGenerationException("Failed to find function ${irFunction.fqNameWhenAvailable!!} with signature ${itemType.toKotlinType()}")

        return irFunctionReference(itemType, functionSymbol).also { call ->
            irFunction.receiverType()!!.classFqName?.let {
                if (irFunction.receiverType()?.getClass()?.isObject != null) {
                    val dispatchSymbol = pluginContext.referenceClass(it)
                        ?: throw ReflektGenerationException("Failed to find receiver class $it")

                    call.dispatchReceiver = irGetObject(dispatchSymbol)
                }
            }
        }
    }

    fun IrBuilderWithScope.irReflektFunctionImplConstructor(
        irSimpleFunctionSymbol: IrSimpleFunctionSymbol,
        itemType: IrSimpleType,
    ): IrFunctionAccessExpression {
        val irFunction = irSimpleFunctionSymbol.owner
        val reference = createFunctionReference(pluginContext, irFunction, itemType)

        return irCall(
            generationSymbols.reflektFunctionImplConstructor,
            typeArguments = listOf(itemType),
            valueArguments = listOf(
                /* function = */ reference,
                /* annotations = */
                irCall(
                    generationSymbols.hashSetOf,
                    typeArguments = listOf(irBuiltIns.annotationType),
                    valueArguments = listOf(
                        /* elements = */
                        irVarargOut(irBuiltIns.annotationType,
                            irFunction.annotations.map { irCall(it.symbol, valueArguments = it.getValueArguments()) }),
                    ),
                ),
                /* name = */ irString(irFunction.kotlinFqName.shortName().toString()),
                /* visibility = */
                irGetEnumValue(
                    generationSymbols.reflektVisibilityClass.defaultType,
                    generationSymbols.reflektVisibilityClass.owner.declarations
                        .filterIsInstance<IrEnumEntry>()
                        .first {
                            it.name == Name.identifier(
                                checkNotNull(irFunction.visibility.toReflektVisibility()) {
                                    "Unsupported visibility of IrSimpleFunction: ${irFunction.visibility}"
                                }.name,
                            )
                        }
                        .symbol,
                ),
                /* isFinal = */ irBoolean(irFunction.modality == Modality.FINAL),
                /* isOpen = */ irBoolean(irFunction.modality == Modality.OPEN),
                /* isAbstract = */ irBoolean(irFunction.modality == Modality.ABSTRACT),
                /* isInline = */ irBoolean(irFunction.isInline),
                /* isExternal = */ irBoolean(irFunction.isExternal),
                /* isOperator = */ irBoolean(irFunction.isOperator),
                /* isInfix = */ irBoolean(irFunction.isInfix),
                /* isSuspend = */ irBoolean(irFunction.isSuspend),
            ),
        )
    }
}
