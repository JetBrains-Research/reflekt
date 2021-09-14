package io.reflekt.plugin.generation.ir

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.ir.SmartReflektInvokeArgumentsCollector
import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.psi.function.toParameterizedType
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import io.reflekt.plugin.generation.common.SmartReflektInvokeParts
import io.reflekt.plugin.scripting.ImportChecker
import io.reflekt.plugin.scripting.KotlinScript
import io.reflekt.plugin.utils.Util.log
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

/* Replaces SmartReflekt invoke calls with their results */
class SmartReflektIrTransformer(
    private val pluginContext: IrPluginContext,
    private val instances: IrReflektInstances,
    private val classpath: List<File>,
    private val messageCollector: MessageCollector? = null
) : BaseReflektIrTransformer(messageCollector) {
    private val importChecker = ImportChecker(classpath)
    private val sources = HashMap<String, SourceFile>()

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

    private fun <T : KtClassOrObject> isSubtypeOfForClassOrObject(classOrObject: T, typeArgumentFqName: String?, binding: BindingContext): Boolean {
        return typeArgumentFqName?.let {
            classOrObject.isSubtypeOf(setOf(typeArgumentFqName), binding)
        } ?: error("Fq name of a type argument for class or object is null")
    }

    private fun isSubtypeOfForFunctions(function: KtNamedFunction, typeArgument: KotlinType?, binding: BindingContext): Boolean {
        return typeArgument?.let {
            function.toParameterizedType(binding)?.isSubtypeOf(typeArgument) ?: false
        } ?: error("A type argument for a function is null")
    }

    private inline fun <reified T, reified I> filterInstances(
        instances: List<IrTypeInstance<T, I>>,
        invokeArguments: TypeArgumentToFilters,
        binding: BindingContext
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
            if (isSubtype && evalFilterBody(imports, invokeArguments.filters, instance)) {
                resultInstances.push(instance)
            }
        }
        return resultInstances
    }

    private inline fun <reified T, reified I> evalFilterBody(
        imports: List<Import>, filters: List<Lambda>,
        instance: IrTypeInstance<T, I>
    ): Boolean {
        for (filter in filters) {
            val result = KotlinScript(
                classpath = classpath,
                imports = imports,
                properties = filter.parameters.zip(listOf(T::class)),
                code = filter.body
            ).eval(listOf(instance.instance)) as Boolean
            if (!result) {
                return false
            }
        }
        return true
    }

    private fun getSourceFile(irFile: IrFile): SourceFile {
        val file = File(irFile.fileEntry.name)
        return sources[file.absolutePath] ?: file.readText().toSourceFile().also {
            sources[file.absolutePath] = it
        }
    }

    private fun String.toSourceFile() = SourceFile(
        imports = lines()
            .filter { it.startsWith("import ") }
            .map { Import(it.removePrefix("import ").trim().removeSuffixIfPresent(".*"), it) },
        content = this
    )
}
