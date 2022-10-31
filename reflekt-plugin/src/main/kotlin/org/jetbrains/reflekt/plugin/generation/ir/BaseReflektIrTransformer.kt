package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.fields
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.common.StorageClassNames
import org.jetbrains.reflekt.plugin.generation.common.BaseReflektInvokeParts
import org.jetbrains.reflekt.plugin.generation.common.ReflektGenerationException
import org.jetbrains.reflekt.plugin.generation.ir.util.*
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.getReflectionKnownHierarchy

/**
 * Contains data stored by Reflekt for each module. The keys are module names, and the values are [IrClassSymbol] for which instances of
 * [org.jetbrains.reflekt.ReflektClass] should be stored.
 */
typealias ModuleStorageClassesMap = MutableMap<Name, StorageClassData>

data class StorageClassData(
    val storageClass: IrClassSymbol,
    val storedClasses: MutableSet<IrClassSymbol> = HashSet(),
    val storedFunctions: MutableSet<IrSimpleFunctionSymbol> = HashSet(),
)

/**
 * A base class for the Reflekt IR transformers.
 *
 * @property pluginContext
 * @property messageCollector
 */
abstract class BaseReflektIrTransformer(
    final override val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector?,
    private val storageClassGenerator: StorageClassGenerator = StorageClassGenerator(pluginContext),
) : IrElementTransformerVoidWithContext(), IrBuilderExtension {
    override val generationSymbols = GenerationSymbols(pluginContext)

    /**
     * Map of storage classes data: keys are module fragments, values are pairs of storage class, and data need to be stored in it afterward.
     */
    val storageClassesData: ModuleStorageClassesMap = HashMap()

    /**
     * Constructs replacement for the result of Reflekt terminal function (toList/toSet/etc.) for classes or objects
     *
     * @param moduleFragment module fragment of the expression.
     * @param invokeParts info about invoke call to retrieve the entity type (objects/classes) and the terminal function (toList/toSet/etc).
     * @param resultValues list of qualified names of objects or classes to return.
     * @param resultType the expected type of the result expression.
     * @return a replacement for a result of terminal function.
     * @throws ReflektGenerationException
     */
    protected fun classOrObjectResultIrCall(
        moduleFragment: IrModuleFragment,
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<IrClass>,
        resultType: IrSimpleType,
    ): IrExpression = IrBuilderWithCurrentScope().run {
        val itemType = requireNotNull(resultType.arguments[0].typeOrNull) { "Return type must have one type argument (e. g. List<T>, Set<T>)" }
        require(invokeParts.entityType == ReflektEntity.CLASSES || invokeParts.entityType == ReflektEntity.OBJECTS) { "Unsupported Reflekt entity" }

        val items = resultValues.map { clazz ->
            val (storageClass, storedClasses) = storageClassesData.getOrPut(moduleFragment.name) {
                StorageClassData(storageClassGenerator.createStorageClass(moduleFragment))
            }

            storedClasses += clazz.getReflectionKnownHierarchy()

            val reflektClassFromMap = irCheckNotNull(
                irMapGet(
                    map = irGetField(
                        receiver = irGetObject(storageClass),
                        field = storageClass.fields.map { it.owner }.first { it.name == StorageClassNames.REFLEKT_CLASSES_NAME },
                    ),
                    key = irClassReference(clazz.symbol),
                ),
            )

            if (invokeParts.entityType == ReflektEntity.OBJECTS) {
                // Boxing to ReflektObject only if needed
                irTypeCast(
                    itemType,
                    irCall(
                        generationSymbols.reflektObjectConstructor,
                        typeArguments = listOf(
                            /* T = */
                            itemType.safeAs<IrSimpleType>()
                                ?.arguments
                                ?.get(0)
                                ?.typeOrNull,
                        ),
                        valueArguments = listOf(reflektClassFromMap),
                    ),
                )
            } else {
                irTypeCast(itemType, reflektClassFromMap)
            }
        }

        return irCall(
            generationSymbols.irTerminalFunction(invokeParts),
            typeArguments = listOf(itemType),
            valueArguments = listOf(irVarargOut(itemType, items)),
        )
    }

    /**
     * Constructs replacement for the result of Reflekt terminal function (toList/toSet/etc) for functions
     *
     * @param invokeParts info about invoke call terminal function (toList/toSet/etc)
     * @param resultValues list of functions' qualified names with additional info to generate the right call
     * @param resultType
     * @return [IrExpression]
     * @throws ReflektGenerationException
     */
    protected fun functionResultIrCall(
        moduleFragment: IrModuleFragment,
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<IrSimpleFunction>,
        resultType: IrSimpleType,
    ): IrExpression = IrBuilderWithCurrentScope().run {
        val itemType = requireNotNull(resultType.arguments[0].typeOrNull) { "Return type must have one type argument (e. g. List<T>, Set<T>)" }
        require(itemType is IrSimpleType) { "itemType is not IrSimpleType" }

        messageCollector?.log("RES ARGS: ${itemType.arguments.map { (it as IrType).classFqName }}")
        messageCollector?.log("size of result values ${resultValues.size}")

        val items = resultValues.map { irFunction ->
            val (storageClass, _, storedFunctions) = storageClassesData.getOrPut(moduleFragment.name) {
                StorageClassData(storageClassGenerator.createStorageClass(moduleFragment))
            }

            storedFunctions.add(irFunction.symbol)
            createFunctionReference(pluginContext, irFunction, itemType)
        }

        return irCall(
            generationSymbols.irTerminalFunction(invokeParts),
            typeArguments = listOf(itemType),
            valueArguments = listOf(irVarargOut(itemType, items)),
        )
    }

    /**
     * [IrBuilderWithScope] to generate IR.
     */
    private inner class IrBuilderWithCurrentScope(scope: Scope = currentScope!!.scope) :
        IrBuilderWithScope(pluginContext, scope, UNDEFINED_OFFSET, UNDEFINED_OFFSET) {

        // no need to pass a body to this object
    }
}
