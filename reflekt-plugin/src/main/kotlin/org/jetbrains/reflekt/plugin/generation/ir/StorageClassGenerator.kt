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
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.reflekt.plugin.analysis.common.ReflektPackage
import org.jetbrains.reflekt.plugin.analysis.common.StorageClassNames
import org.jetbrains.reflekt.plugin.generation.ir.util.*
import org.jetbrains.reflekt.plugin.utils.getImmediateSuperclasses

/**
 * Generates `object` classes to store instances of [org.jetbrains.reflekt.ReflektClass]. The generation is done in two steps:
 * 1. A blank storage class is created with [createStorageClass].
 * 2. Then it is filled by [contributeInitializer] provided classes data about which are stored.
 *
 * @property pluginContext
 */
class StorageClassGenerator(override val pluginContext: IrPluginContext) : IrBuilderExtension {
    private val irFactory = pluginContext.irFactory
    override val generationSymbols = GenerationSymbols(pluginContext)
    private val mVariableName = Name.identifier("m")

    private fun syntheticFile(packageFragmentDescriptor: PackageFragmentDescriptor, name: String, module: IrModuleFragment): IrFile =
        IrFileImpl(NaiveSourceBasedFileEntryImpl(name), packageFragmentDescriptor, module).also { module.files += it }

    fun createStorageClass(moduleFragment: IrModuleFragment): IrClassSymbol {
        // Names of storage class are chosen to avoid duplication.
        val idx = generateSequence(0) { it + 1 }.first { pluginContext.referenceClass(FqName("${ReflektPackage.PACKAGE_NAME}.Storage_$it")) == null }
        val file = syntheticFile(EmptyPackageFragmentDescriptor(moduleFragment.descriptor, ReflektPackage.PACKAGE_FQ_NAME), "Storage_$idx", moduleFragment)

        // Initially, storage class contains:
        // 1. thisReceiver and typical constructor
        // 2. Field storing Map<KClass<*>, ReflektClass<*>>
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
                name = StorageClassNames.REFLEKT_CLASSES_NAME
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

    private fun IrBlockBodyBuilder.addStoredClassRelations(
        storedClass: IrClassSymbol,
        relatedClasses: Iterable<IrClassSymbol>,
        mapVariable: IrValueDeclaration,
        getMutableSetToFill: IrFunctionSymbol,
        variance: Variance,
    ) = relatedClasses.forEach { subclass ->
        +irMutableSetAdd(
            mutableSet = irCall(
                getMutableSetToFill,
                dispatchReceiver = irTypeCast(
                    type = generationSymbols.reflektClassImplClass.createType(false, listOf(storedClass.owner.defaultType)),
                    castTo = irCheckNotNull(
                        value = irMapGet(
                            map = irGet(mapVariable),
                            key = irClassReference(storedClass),
                        ),
                    ),
                ),
            ),
            element = irTypeCast(
                type = generationSymbols.reflektClassClass.createType(
                    false,
                    listOf(makeTypeProjection(storedClass.defaultType, variance)),
                ),
                castTo = irCheckNotNull(
                    value = irMapGet(
                        map = irGet(mapVariable),
                        key = irClassReference(subclass),
                    ),
                ),
            ),
        )
    }

    @Suppress("LongMethod", "TOO_LONG_FUNCTION")
    fun contributeInitializer(storageClassSymbol: IrClassSymbol, storedClassesSymbols: Collection<IrClassSymbol>) {
        val storageClass = storageClassSymbol.owner
        val storedClasses = storedClassesSymbols.map { it.owner }

        // Adding an anonymous initializer filling storage field with data from storedClassesSymbols.
        storageClass.contributeAnonymousInitializer {
            // At the first, ReflektClassImpl is instantiated for each stored class without superclasses and sealed subclasses data and is stored to HashMap.
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
            // Then, add calls are generated to set up superclasses and sealed subclasses for each stored class.
            for (storedClass in storedClasses) {
                addStoredClassRelations(
                    storedClass = storedClass.symbol,
                    relatedClasses = storedClass.getImmediateSuperclasses(),
                    mapVariable = mVariable,
                    getMutableSetToFill = generationSymbols.reflektClassImplGetSuperclasses,
                    variance = Variance.IN_VARIANCE,
                )
            }

            for (storedClass in storedClasses) {
                addStoredClassRelations(
                    storedClass = storedClass.symbol,
                    relatedClasses = storedClass.sealedSubclasses,
                    mapVariable = mVariable,
                    getMutableSetToFill = generationSymbols.reflektClassImplGetSealedSubclasses,
                    variance = Variance.OUT_VARIANCE,
                )
            }

            // Created HashMap is stored to the data field.
            +irSetField(
                irGet(storageClass.thisReceiver!!),
                storageClass.symbol.fieldByName(StorageClassNames.REFLEKT_CLASSES).owner,
                irGet(mVariable),
            )
        }
    }
}
