package org.jetbrains.reflekt.plugin.generation.ir

import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.ir.SmartReflektInvokeArgumentsCollector
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrReflektInstances
import org.jetbrains.reflekt.plugin.analysis.models.ir.IrTypeInstance
import org.jetbrains.reflekt.plugin.analysis.psi.function.toParameterizedType
import org.jetbrains.reflekt.plugin.analysis.psi.isSubtypeOf
import org.jetbrains.reflekt.plugin.generation.common.SmartReflektInvokeParts
import org.jetbrains.reflekt.plugin.scripting.ImportChecker
import org.jetbrains.reflekt.plugin.scripting.KotlinScriptRunner
import org.jetbrains.reflekt.plugin.utils.Util.log

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.util.removeSuffixIfPresent

import java.io.File

/**
 * Replaces SmartReflekt invoke calls with their results.
 *
 * @property pluginContext
 * @property instances stores all public instances (classes, objects, and top-level functions) in the project
 * @property classpath project dependencies that can be resolved at the compile time
 * @property messageCollector
 * @property importChecker [ImportChecker] for filtering classpath for correct running the KotlinScript interpreter
 * @property sources map of absolute paths of [IrFile.fileEntry] to its content and imports.
 *  It is used for collecting arguments for the SmartRefelkt queries
 */
@Suppress("KDOC_EXTRA_PROPERTY", "KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER")
class SmartReflektIrTransformer(
    private val pluginContext: IrPluginContext,
    private val instances: IrReflektInstances,
    private val classpath: List<File>,
    private val messageCollector: MessageCollector? = null,
) : BaseReflektIrTransformer(messageCollector) {
    private val importChecker = ImportChecker(classpath)
    private val sources = HashMap<String, SourceFile>()

    /**
     * Visits [IrCall] and replaces IR to found entities if it is a SmartReflekt query.
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
                    filterInstances(instances.objects, invokeArguments, pluginContext.bindingContext)
                } else {
                    filterInstances(instances.classes, invokeArguments, pluginContext.bindingContext)
                }
                newIrBuilder(pluginContext).resultIrCall(invokeParts, filteredInstances.map { it.info }, expression.type, pluginContext)
            }
            ReflektEntity.FUNCTIONS -> {
                val filteredInstances = filterInstances(instances.functions, invokeArguments, pluginContext.bindingContext)
                newIrBuilder(pluginContext).functionResultIrCall(invokeParts, filteredInstances.map { it.info }, expression.type, pluginContext)
            }
        }
        messageCollector?.log("GENERATE CALL:\n${call.dump()}")
        return call
    }

    /**
     * Checks if the [typeArgumentFqName] is subtype of [classOrObject].
     *
     * @param classOrObject
     * @param typeArgumentFqName
     * @param binding
     * @return {@code true} if the [typeArgumentFqName]  is subtype of [classOrObject]
     */
    // TODO: replace to IrTypes, we should not use BindingContext since in the new compiler versions with new frontend it will be deleted
    private fun <T : KtClassOrObject> isSubtypeOfForClassOrObject(
        classOrObject: T,
        typeArgumentFqName: String?,
        binding: BindingContext) = typeArgumentFqName?.let {
        classOrObject.isSubtypeOf(setOf(typeArgumentFqName), binding)
    } ?: error("Fq name of a type argument for class or object is null")

    /**
     * Checks if the [typeArgument] is subtype of [function].
     *
     * @param function
     * @param typeArgument
     * @param binding
     * @return {@code true} if the [typeArgument]  is subtype of [function]
     */
    // TODO: replace to IrTypes, we should not use BindingContext since in the new compiler versions with new frontend it will be deleted
    private fun isSubtypeOfForFunctions(
        function: KtNamedFunction,
        typeArgument: KotlinType?,
        binding: BindingContext) = typeArgument?.let {
        function.toParameterizedType(binding)?.isSubtypeOf(typeArgument) ?: false
    } ?: error("A type argument for a function is null")

    /**
     * Filters [instances] that satisfy [invokeArguments].
     *
     * @param instances
     * @param invokeArguments
     * @param binding
     * @return list of [IrTypeInstance] that satisfy [invokeArguments]
     */
    // TODO: replace to IrTypes, we should not use BindingContext since in the new compiler versions with new frontend it will be deleted
    @Suppress("TYPE_ALIAS")
    private inline fun <reified T, reified I> filterInstances(
        instances: List<IrTypeInstance<T, I>>,
        invokeArguments: TypeArgumentToFilters,
        binding: BindingContext,
    ): List<IrTypeInstance<T, I>> {
        val imports = importChecker.filterImports(invokeArguments.imports)

        val resultInstances = ArrayList<IrTypeInstance<T, I>>()
        for (instance in instances) {
            val isSubtype = when (instance.instance) {
                is KtObjectDeclaration -> isSubtypeOfForClassOrObject(instance.instance, invokeArguments.typeArgumentFqName, binding)
                is KtClass -> isSubtypeOfForClassOrObject(instance.instance, invokeArguments.typeArgumentFqName, binding)
                is KtNamedFunction -> isSubtypeOfForFunctions(instance.instance, invokeArguments.typeArgument, binding)
                else -> error("Unknown type of instance")
            }
            if (isSubtype && isEvaluatedFilterBody(imports, invokeArguments.filters, instance)) {
                resultInstances.push(instance)
            }
        }
        return resultInstances
    }

    /**
     * Checks if [instance] satisfies list of [filters].
     *
     * @param imports for KotlinScript running
     * @param filters
     * @param instance
     * @return {@code true} if [instance] satisfies list of [filters]
     */
    // TODO: run KotlinScript on a list of instances
    private inline fun <reified T, reified I> isEvaluatedFilterBody(
        imports: List<Import>,
        filters: List<Lambda>,
        instance: IrTypeInstance<T, I>,
    ): Boolean {
        for (filter in filters) {
            val result = KotlinScriptRunner(
                classpath = classpath,
                imports = imports,
                properties = filter.parameters.zip(listOf(T::class)),
                code = filter.body,
            ).eval(listOf(instance.instance)) as Boolean
            if (!result) {
                return false
            }
        }
        return true
    }

    /**
     * Gets [SourceFile] by [IrFile].
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
     * Constructs [SourceFile] manually from [String] (extracts imports and its content).
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
