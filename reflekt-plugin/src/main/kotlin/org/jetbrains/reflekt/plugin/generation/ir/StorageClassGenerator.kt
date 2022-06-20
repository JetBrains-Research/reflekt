package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.EmptyPackageFragmentDescriptor
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrFileImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.reflekt.plugin.analysis.common.ReflektClassRegistry
import org.jetbrains.reflekt.plugin.analysis.processor.toReflektVisibility
import org.jetbrains.reflekt.plugin.generation.ir.util.*
import org.jetbrains.reflekt.plugin.utils.getImmediateSuperclasses
import org.jetbrains.reflekt.plugin.utils.getValueArguments

/**
 * @property pluginContext
 */
class StorageClassGenerator(override val pluginContext: IrPluginContext) : IrBuilderExtension {
    private val irFactory = pluginContext.irFactory
    private val generationSymbols = GenerationSymbols(pluginContext)
    private val kClassAndReflektClassStarProjections = listOf(irBuiltIns.kClassClass.starProjectedType, generationSymbols.reflektClassClass.starProjectedType)
    private val mVariableName = Name.identifier("m")

    private fun syntheticFile(packageFragmentDescriptor: PackageFragmentDescriptor, name: String, module: IrModuleFragment): IrFile =
        IrFileImpl(NaiveSourceBasedFileEntryImpl(name), packageFragmentDescriptor, module).also { module.files += it }

    fun createStorageClass(moduleFragment: IrModuleFragment): IrClassSymbol {
        val idx = generateSequence(0) { it + 1 }.first { pluginContext.referenceClass(FqName("$ORG_JETBRAINS_REFLECT.Storage_$it")) == null }
        val file = syntheticFile(EmptyPackageFragmentDescriptor(moduleFragment.descriptor, orgJetbrainsReflektFqName), "Storage_$idx", moduleFragment)

        return irFactory.buildClass {
            visibility = DescriptorVisibilities.INTERNAL
            kind = ClassKind.OBJECT
            name = Name.identifier("Storage_$idx")
        }.also { irClass ->
            file.addChild(irClass)

            irClass.thisReceiver =
                buildReceiverParameter(irClass, IrDeclarationOrigin.INSTANCE_RECEIVER, irClass.symbol.typeWithParameters(irClass.typeParameters))

            irClass.superTypes = listOf(irBuiltIns.anyType)

            irClass.addConstructor {
                visibility = DescriptorVisibilities.PRIVATE
                returnType = irClass.symbol.createType(false, listOf())
                isPrimary = true
            }.also { constructor ->
                val irBuilder = DeclarationIrBuilder(pluginContext, constructor.symbol, constructor.startOffset, constructor.endOffset)
                constructor.body = irBuilder.irBlockBody {
                    +irDelegatingConstructorCall(generationSymbols.anyConstructor.owner)
                    +IrInstanceInitializerCallImpl(startOffset, endOffset, irClass.symbol, constructor.returnType)
                }
            }

            irClass.addField {
                name = ReflektClassRegistry.REFLEKT_CLASSES.propertyNameName
                type = irBuiltIns.mapClass.createType(
                    false,
                    listOf(irBuiltIns.kClassClass.starProjectedType, generationSymbols.reflektClassClass.starProjectedType),
                )
            }.also { field ->
                val irBuilder = DeclarationIrBuilder(pluginContext, field.symbol, field.startOffset, field.endOffset)
                field.annotations =
                    listOf(irBuilder.irCall(generationSymbols.jvmSyntheticConstructor), irBuilder.irCall(generationSymbols.jvmFieldConstructor))
            }
        }.symbol
    }

    @Suppress("LongMethod", "TOO_LONG_FUNCTION")
    fun contributeInitializers(storageClasses: ModuleStorageClassesMap) {
        storageClasses.values.asSequence().map { (a, b) -> a.owner to b.map { it.owner } }.forEach { (storageClass, storedClasses) ->
            storageClass.contributeAnonymousInitializer {
                val mVariable = irVariableVal(
                    parent = storageClass, name = mVariableName, type = generationSymbols.hashMapClass.createType(false, kClassAndReflektClassStarProjections),
                    isConst = false, isLateinit = false,
                ).also { variable ->
                    variable.initializer = irCall(
                        generationSymbols.hashMapOf,
                        typeArguments = kClassAndReflektClassStarProjections,
                        valueArguments = listOf(
                            irVarargOut(
                                generationSymbols.pairClass.createType(false, kClassAndReflektClassStarProjections),
                                storedClasses.map { storedClass ->
                                    irCall(
                                        generationSymbols.to,
                                        typeArguments = kClassAndReflektClassStarProjections,
                                        extensionReceiver = irClassReference(storedClass.symbol),
                                        valueArguments = listOf(
                                            irCall(
                                                generationSymbols.reflektClassImplConstructor,
                                                typeArguments = listOf(storedClass.symbol.defaultType),
                                                valueArguments = listOf(
                                                    irClassReference(storedClass.symbol),
                                                    irCall(
                                                        generationSymbols.hashSetOf,
                                                        typeArguments = listOf(irBuiltIns.annotationType),
                                                        valueArguments = listOf(
                                                            irVarargOut(
                                                                irBuiltIns.annotationType,
                                                                storedClass.annotations.map { irCall(it.symbol, valueArguments = it.getValueArguments()) }),
                                                        ),
                                                    ),
                                                    irBoolean(storedClass.modality == Modality.ABSTRACT),
                                                    irBoolean(storedClass.isCompanion),
                                                    irBoolean(storedClass.isData),
                                                    irBoolean(storedClass.modality == Modality.FINAL),
                                                    irBoolean(storedClass.isFun),
                                                    irBoolean(storedClass.isInner),
                                                    irBoolean(storedClass.modality == Modality.OPEN),
                                                    irBoolean(storedClass.modality == Modality.SEALED),
                                                    irBoolean(storedClass.isValue),
                                                    irString(storedClass.kotlinFqName.toString()),
                                                    irCall(generationSymbols.hashSetConstructor),
                                                    irCall(generationSymbols.hashSetConstructor),
                                                    irString(storedClass.kotlinFqName.shortName().toString()),
                                                    irGetEnumValue(
                                                        generationSymbols.reflektVisibilityClass.defaultType,
                                                        generationSymbols.reflektVisibilityClass.owner.declarations.filterIsInstance<IrEnumEntry>()
                                                            .first { it.name == Name.identifier(storedClass.visibility.toReflektVisibility()!!.name) }
                                                            .symbol,
                                                    ),
                                                ),
                                            ),
                                        ),
                                    )
                                },
                            ),
                        )
                    )
                }
                +mVariable
                for (storedClass in storedClasses) {
                    storedClass.getImmediateSuperclasses().forEach { superclass ->
                        +irCall(
                            generationSymbols.mutableSetAdd,
                            dispatchReceiver = irCall(
                                generationSymbols.reflektClassImplGetSuperclasses,
                                dispatchReceiver = irTypeCast(
                                    generationSymbols.reflektClassImplClass.createType(false, listOf(storedClass.defaultType)),
                                    irCheckNotNull(
                                        irCall(
                                            generationSymbols.mapGet,
                                            dispatchReceiver = irGet(mVariable),
                                            valueArguments = listOf(irClassReference(storedClass.symbol)),
                                        ),
                                    ),
                                ),
                            ),
                            valueArguments = listOf(
                                irTypeCast(
                                    generationSymbols.reflektClassClass.createType(
                                        false,
                                        listOf(makeTypeProjection(storedClass.defaultType, Variance.IN_VARIANCE)),
                                    ),
                                    irCheckNotNull(
                                        irCall(
                                            generationSymbols.mapGet,
                                            dispatchReceiver = irGet(mVariable),
                                            valueArguments = listOf(irClassReference(superclass)),
                                        ),
                                    ),
                                ),
                            ),
                        )
                    }
                }

                for (storedClass in storedClasses) {
                    storedClass.sealedSubclasses.forEach { subclass ->
                        +irCall(
                            generationSymbols.mutableSetAdd,
                            dispatchReceiver = irCall(
                                generationSymbols.reflektClassImplGetSealedSubclasses,
                                dispatchReceiver = irTypeCast(
                                    generationSymbols.reflektClassImplClass.createType(false, listOf(storedClass.defaultType)),
                                    irCheckNotNull(
                                        irCall(
                                            generationSymbols.mapGet,
                                            dispatchReceiver = irGet(mVariable),
                                            valueArguments = listOf(irClassReference(storedClass.symbol)),
                                        ),
                                    ),
                                ),
                            ),
                            valueArguments = listOf(
                                irTypeCast(
                                    generationSymbols.reflektClassClass.createType(
                                        false,
                                        listOf(makeTypeProjection(storedClass.defaultType, Variance.OUT_VARIANCE)),
                                    ),
                                    irCheckNotNull(
                                        irCall(
                                            generationSymbols.mapGet,
                                            dispatchReceiver = irGet(mVariable),
                                            valueArguments = listOf(irClassReference(subclass)),
                                        ),
                                    ),
                                ),
                            ),
                        )
                    }
                }

                +irSetField(
                    irGet(storageClass.thisReceiver!!),
                    storageClass.fields.first { it.name == ReflektClassRegistry.REFLEKT_CLASSES.propertyNameName },
                    irGet(mVariable),
                )
            }
        }
    }

    private companion object {
        private const val ORG_JETBRAINS_REFLECT = "org.jetbrains.reflekt"
        private val orgJetbrainsReflektFqName = FqName("org.jetbrains.reflekt")
    }
}
