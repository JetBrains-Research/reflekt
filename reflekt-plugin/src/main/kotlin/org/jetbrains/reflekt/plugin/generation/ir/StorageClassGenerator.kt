package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.jvm.fieldByName
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
import org.jetbrains.reflekt.plugin.analysis.common.StorageClassProperties
import org.jetbrains.reflekt.plugin.generation.ir.util.*
import org.jetbrains.reflekt.plugin.utils.getImmediateSuperclasses

/**
 * @property pluginContext
 */
class StorageClassGenerator(override val pluginContext: IrPluginContext) : IrBuilderExtension {
    private val irFactory = pluginContext.irFactory
    override val generationSymbols = GenerationSymbols(pluginContext)
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
                name = StorageClassProperties.REFLEKT_CLASSES.propertyNameName
                type = irBuiltIns.mapClass.createType(
                    false,
                    listOf(irBuiltIns.kClassClass.starProjectedType, generationSymbols.reflektClassClass.starProjectedType),
                )
            }.also { field ->
                val irBuilder = DeclarationIrBuilder(pluginContext, field.symbol, field.startOffset, field.endOffset)
                field.annotations = listOf(irBuilder.irCall(generationSymbols.jvmSyntheticConstructor), irBuilder.irCall(generationSymbols.jvmFieldConstructor))
            }
        }.symbol
    }

    @Suppress("LongMethod", "TOO_LONG_FUNCTION")
    fun contributeInitializers(storageClasses: ModuleStorageClassesMap) {
        storageClasses.values.asSequence().map { (a, b) -> a.owner to b.map { it.owner } }.forEach { (storageClass, storedClasses) ->
            storageClass.contributeAnonymousInitializer {
                val mVariable = irVariableVal(
                    parent = storageClass,
                    name = mVariableName,
                    type = generationSymbols.hashMapClass.createType(
                        false,
                        listOf(irBuiltIns.kClassClass.starProjectedType, generationSymbols.reflektClassClass.starProjectedType)
                    ),
                    isConst = false,
                    isLateinit = false,
                ).also { variable ->
                    variable.initializer = irHashMapOf(
                        keyType = irBuiltIns.kClassClass.starProjectedType,
                        valueType = generationSymbols.reflektClassClass.starProjectedType,
                        pairs = storedClasses.map { storedClass ->
                            irTo(left = irClassReference(storedClass.symbol), right = irReflektClassImplConstructor(storedClass.symbol))
                        },
                    )
                }
                +mVariable
                for (storedClass in storedClasses) {
                    storedClass.getImmediateSuperclasses().forEach { superclass ->
                        +irMutableSetAdd(
                            mutableSet = irCall(
                                generationSymbols.reflektClassImplGetSuperclasses,
                                dispatchReceiver = irTypeCast(
                                    generationSymbols.reflektClassImplClass.createType(false, listOf(storedClass.defaultType)),
                                    irCheckNotNull(irMapGet(map = irGet(mVariable), key = irClassReference(storedClass.symbol))),
                                ),
                            ),
                            element = irTypeCast(
                                generationSymbols.reflektClassClass.createType(
                                    false,
                                    listOf(makeTypeProjection(storedClass.defaultType, Variance.IN_VARIANCE)),
                                ),
                                irCheckNotNull(
                                    value = irMapGet(
                                        map = irGet(mVariable),
                                        key = irClassReference(superclass),
                                    ),
                                ),
                            ),
                        )
                    }
                }

                for (storedClass in storedClasses) {
                    storedClass.sealedSubclasses.forEach { subclass ->
                        +irMutableSetAdd(
                            mutableSet = irCall(
                                generationSymbols.reflektClassImplGetSealedSubclasses,
                                dispatchReceiver = irTypeCast(
                                    type = generationSymbols.reflektClassImplClass.createType(false, listOf(storedClass.defaultType)),
                                    castTo = irCheckNotNull(
                                        value = irMapGet(
                                            map = irGet(mVariable),
                                            key = irClassReference(storedClass.symbol),
                                        ),
                                    ),
                                ),
                            ),
                            element = irTypeCast(
                                type = generationSymbols.reflektClassClass.createType(
                                    false,
                                    listOf(makeTypeProjection(storedClass.defaultType, Variance.OUT_VARIANCE)),
                                ),
                                castTo = irCheckNotNull(
                                    value = irMapGet(
                                        map = irGet(mVariable),
                                        key = irClassReference(subclass),
                                    ),
                                ),
                            ),
                        )
                    }
                }

                +irSetField(
                    irGet(storageClass.thisReceiver!!),
                    storageClass.symbol.fieldByName(StorageClassProperties.REFLEKT_CLASSES.propertyNameString).owner,
                    irGet(mVariable),
                )
            }
        }
    }

    private companion object {
        private const val ORG_JETBRAINS_REFLECT = "org.jetbrains.reflekt"
        private val orgJetbrainsReflektFqName = FqName(ORG_JETBRAINS_REFLECT)
    }
}
