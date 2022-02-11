package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.util.removeSuffixIfPresent
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.ir.*
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.ir.*
import org.jetbrains.reflekt.plugin.generation.common.SmartReflektInvokeParts
import org.jetbrains.reflekt.plugin.scripting.ImportChecker
import org.jetbrains.reflekt.plugin.scripting.KotlinScriptRunner
import org.jetbrains.reflekt.plugin.utils.Util.log
import java.io.File
import kotlin.reflect.KClass

/**
 * Replaces SmartReflekt invoke calls with their results
 *
 * @property irInstances
 * @property pluginContext
 * @property classpath project dependencies that can be resolved at the compile time
 * @property messageCollector
 * @property importChecker [ImportChecker] for filtering classpath for correct running the KotlinScript interpreter
 * @property sources map of absolute paths of [IrFile.fileEntry] to its content and imports.
 *  It is used for collecting arguments for the SmartRefelkt queries
 */
@Suppress("KDOC_EXTRA_PROPERTY", "KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER")
class SmartReflektIrTransformer(
    private val irInstances: IrInstances,
    private val pluginContext: IrPluginContext,
    private val classpath: List<File>,
    private val messageCollector: MessageCollector? = null,
) : BaseReflektIrTransformer(messageCollector) {
    private val importChecker = ImportChecker(classpath)
    private val sources = HashMap<String, SourceFile>()

    /**
     * Visit [IrCall] and replace IR to found entities if it is a SmartReflekt query
     *
     * @param expression [IrCall]
     */
    @ObsoleteDescriptorBasedAPI
    override fun visitCall(expression: IrCall): IrExpression {
        val function = expression.symbol.owner
        val expressionFqName = function.fqNameForIrSerialization.toString()
        val invokeParts = SmartReflektInvokeParts.parse(expressionFqName) ?: return super.visitCall(expression)
        messageCollector?.log("[IR] SMART REFLEKT CALL: $expressionFqName;")

        val invokeArguments = SmartReflektInvokeArgumentsCollector.collectInvokeArguments(expression, getSourceFile(currentFile))
        val call = when (invokeParts.entityType) {
            ReflektEntity.OBJECTS, ReflektEntity.CLASSES -> {
                val filteredInstances = if (invokeParts.entityType == ReflektEntity.OBJECTS) {
                    filterInstances(irInstances.objects, invokeArguments)
                } else {
                    filterInstances(irInstances.classes, invokeArguments)
                }
                newIrBuilder(pluginContext).resultIrCall(
                    invokeParts,
                    filteredInstances.mapNotNull { (it as? IrClass)?.fqNameWhenAvailable?.asString() },
                    expression.type,
                    pluginContext,
                )
            }
            ReflektEntity.FUNCTIONS -> {
                val filteredInstances = filterInstances(irInstances.functions, invokeArguments)
                newIrBuilder(pluginContext).functionResultIrCall(
                    invokeParts,
                    filteredInstances.mapNotNull { (it as? IrFunction)?.toFunctionInfo() },
                    expression.type,
                    pluginContext,
                )
            }
        }
        messageCollector?.log("GENERATE CALL:\n${call.dump()}")
        return call
    }

    /**
     * Check if [IrElement] is subtype of [type] (only for [IrClass] and [IrFunction], in other cases are [false])
     *
     * @param type
     * @return {@code true} if [IrElement] is subtype of [type]
     */
    private fun IrElement.isSubTypeOrFalse(type: IrType?) = type?.let {
        when (this) {
            is IrClass -> this.isSubTypeOf(type, pluginContext)
            is IrFunction -> this.isSubTypeOf(type, pluginContext)
            else -> false
        }
    } ?: false

    /**
     * Check if the list of [instances] satisfy SmartReflekt conditions from [invokeArguments]
     *
     * @param instances
     * @param invokeArguments
     */
    private fun filterInstances(
        instances: List<IrElement>,
        invokeArguments: TypeArgumentToFilters,
    ): List<IrElement> {
        val imports = importChecker.filterImports(invokeArguments.imports)
        return instances.filter { it.isSubTypeOrFalse(invokeArguments.irTypeArgument) && it.isEvaluatedFilterBody(imports, invokeArguments.filters, it::class) }
    }

    /**
     * Check if instance [T] satisfies list of [filters]
     *
     * @param imports for KotlinScript running
     * @param filters
     * @return {@code true} if instance [T] satisfies list of [filters]
     */
    // TODO: union filters and run KotlinScript one time
    private fun <T : IrElement> T.isEvaluatedFilterBody(
        imports: List<Import>,
        filters: List<Lambda>,
        elementClass: KClass<out IrElement>
    ): Boolean {
        for (filter in filters) {
            val result = KotlinScriptRunner(
                classpath = classpath,
                imports = imports,
                properties = filter.parameters.zip(listOf(elementClass)),
                code = filter.body,
            ).eval(listOf(this)) as Boolean
            if (!result) {
                return false
            }
        }
        return true
    }

    /**
     * Get [SourceFile] by [IrFile]
     *
     * @param irFile
     * @return [SourceFile]
     */
    private fun getSourceFile(irFile: IrFile): SourceFile {
        val file = File(irFile.fileEntry.name)
        return sources[file.absolutePath] ?: file.readText().toSourceFile().also {
            sources[file.absolutePath] = it
        }
    }

    /**
     * Construct [SourceFile] manually from [String] (extract imports and its content)
     *
     * @return [SourceFile]
     */
    private fun String.toSourceFile() = SourceFile(
        imports = lines()
            .filter { it.startsWith("import ") }
            .map { Import(it.removePrefix("import ").trim().removeSuffixIfPresent(".*"), it) },
        content = this,
    )
}
