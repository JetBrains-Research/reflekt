package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.fields
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.common.StorageClassNames
import org.jetbrains.reflekt.plugin.analysis.ir.isSubtypeOf
import org.jetbrains.reflekt.plugin.analysis.ir.toParameterizedType
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrFunctionInfo
import org.jetbrains.reflekt.plugin.generation.common.BaseReflektInvokeParts
import org.jetbrains.reflekt.plugin.generation.common.ReflektGenerationException
import org.jetbrains.reflekt.plugin.generation.ir.util.*
import org.jetbrains.reflekt.plugin.utils.Util.log
import org.jetbrains.reflekt.plugin.utils.getReflectionKnownHierarchy

/**
 * Contains data stored by Reflekt for each module. The keys are module names, and the values are [IrClassSymbol] for which instances of
 * [org.jetbrains.reflekt.ReflektClass] should be stored.
 */
typealias ModuleStorageClassesMap = MutableMap<Name, Pair<IrClassSymbol, MutableSet<IrClassSymbol>>>

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
     * Map of stored class data: keys are module fragments, values are pairs of storage class and data need to be stored in it afterward.
     */
    val storageClasses: ModuleStorageClassesMap = HashMap()

    /**
     * Constructs replacement for result of Reflekt terminal function (toList/toSet/etc.) for classes or objects
     *
     * @param moduleFragment module fragment of the expression.
     * @param invokeParts info about invoke call to retrieve entity type (objects/classes) and terminal function (toList/toSet/etc).
     * @param resultValues list of qualified names of objects or classes to return.
     * @param resultType
     * @return replacement for a result of terminal function.
     * @throws ReflektGenerationException
     */
    protected fun resultIrCall(
        moduleFragment: IrModuleFragment,
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<String>,
        resultType: IrType,
    ): IrExpression = IrBuilderWithCurrentScope().run {
        require(resultType is IrSimpleType) { "resultType is not IrSimpleType" }

        val itemType = resultType.arguments[0].typeOrNull
            ?: throw ReflektGenerationException("Return type must have one type argument (e. g. List<T>, Set<T>)")

        val items = resultValues
            .map { pluginContext.referenceClass(FqName(it)) ?: throw ReflektGenerationException("Failed to find class $it") }
            .map { classSymbol ->
                when (invokeParts.entityType) {
                    ReflektEntity.OBJECTS, ReflektEntity.CLASSES -> {
                        val (storageClass, storageClassData) =
                            storageClasses.getOrPut(moduleFragment.name) { storageClassGenerator.createStorageClass(moduleFragment) to HashSet() }
                        storageClassData += classSymbol.owner.getReflectionKnownHierarchy()

                        val reflektClassFromMap = irCheckNotNull(
                            irMapGet(
                                map = irGetField(
                                    irGetObject(storageClass),
                                    storageClass.fields.map { it.owner }.first { it.name == StorageClassNames.REFLEKT_CLASSES_NAME },
                                ),
                                key = irClassReference(classSymbol),
                            ),
                        )

                        if (invokeParts.entityType == ReflektEntity.OBJECTS) {
                            // Boxing to ReflektObject only if needed
                            irTypeCast(
                                itemType,
                                irCall(
                                    generationSymbols.reflektObjectConstructor,
                                    typeArguments = listOf(
                                        itemType.safeAs<IrSimpleType>()
                                            ?.arguments
                                            ?.get(0)
                                            ?.typeOrNull,
                                    ),
                                    valueArguments = listOf(reflektClassFromMap),
                                )
                            )
                        } else {
                            irTypeCast(itemType, reflektClassFromMap)
                        }
                    }

                    ReflektEntity.FUNCTIONS -> error("Use functionResultIrCall")
                }
            }

        return irCall(
            generationSymbols.irTerminalFunction(invokeParts),
            typeArguments = listOf(itemType),
            valueArguments = listOf(irVarargOut(itemType, items)),
        )
    }

    /**
     * Constructs replacement for result of Reflekt terminal function (toList/toSet/etc) for functions
     *
     * @param invokeParts info about invoke call terminal function (toList/toSet/etc)
     * @param resultValues list of function qualified names with additional info to generate the right call
     * @param resultType
     * @return [IrExpression]
     * @throws ReflektGenerationException
     */
    @Suppress("TOO_MANY_LINES_IN_LAMBDA", "ThrowsCount")
    protected fun functionResultIrCall(
        invokeParts: BaseReflektInvokeParts,
        resultValues: List<IrFunctionInfo>,
        resultType: IrType,
    ): IrExpression = IrBuilderWithCurrentScope().run {
        require(resultType is IrSimpleType) { "resultType is not IrSimpleType" }

        val itemType = resultType.arguments[0].typeOrNull
            ?: throw ReflektGenerationException("Return type must have one type argument (e. g. List<T>, Set<T>)")

        require(itemType is IrSimpleType)

        messageCollector?.log("RES ARGS: ${itemType.arguments.map { (it as IrSimpleType).classFqName }}")
        messageCollector?.log("size of result values ${resultValues.size}")
        val items = resultValues.map { irFunctionInfo ->
            val functionSymbol = pluginContext.referenceFunctions(FqName(irFunctionInfo.fqName)).firstOrNull { symbol ->
                symbol.owner.isSubtypeOf(itemType, pluginContext).also { messageCollector?.log("${symbol.owner.isSubtypeOf(itemType, pluginContext)}") }
            }
            messageCollector?.log("function symbol is $functionSymbol")
            functionSymbol ?: run {
                messageCollector?.log("function symbol is null")
                throw ReflektGenerationException("Failed to find function ${irFunctionInfo.fqName} with signature ${itemType.toParameterizedType()}")
            }
            irKFunction(itemType, functionSymbol).also { call ->
                irFunctionInfo.receiverFqName?.let {
                    if (irFunctionInfo.isObjectReceiver) {
                        val dispatchSymbol = pluginContext.referenceClass(FqName(irFunctionInfo.receiverFqName))
                            ?: throw ReflektGenerationException("Failed to find receiver class ${irFunctionInfo.receiverFqName}")
                        call.dispatchReceiver = irGetObject(dispatchSymbol)
                    }
                }
            }
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
    private inner class IrBuilderWithCurrentScope(scope: Scope = currentScope!!.scope) : IrBuilderWithScope(
        pluginContext,
        scope,
        UNDEFINED_OFFSET,
        UNDEFINED_OFFSET,
    ) {
        // no need to pass a body to this object
    }
}
