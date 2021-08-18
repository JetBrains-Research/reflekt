package io.reflekt.plugin.generation.bytecode

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.common.findSmartReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.models.SupertypesToFilters
import io.reflekt.plugin.analysis.psi.function.checkSignature
import io.reflekt.plugin.analysis.psi.function.fqName
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import io.reflekt.plugin.generation.bytecode.util.genAsmType
import io.reflekt.plugin.generation.common.ReflektGenerationException
import io.reflekt.plugin.generation.common.SmartReflektInvokeParts
import io.reflekt.plugin.scripting.ImportChecker
import io.reflekt.plugin.scripting.KotlinScript
import io.reflekt.plugin.utils.Util.getInstances
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import java.io.File

class SmartReflektGeneratorExtension(
    private val classpath: List<File>,
    private val messageCollector: MessageCollector? = null
) : BaseReflektGeneratorExtension() {
    override val functionInstanceGenerator = FunctionInstanceGenerator("io/reflekt/generated/SmartFunctions", messageCollector)
    private val importChecker = ImportChecker(classpath)

    override fun myApplyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        val binding = c.codegen.bindingContext
        val expression = resolvedCall.call.calleeExpression ?: return null

        val expressionFqName = expression.getFqName(c.codegen.bindingContext) ?: return null

        // Split expression into known parts of SmartReflekt invoke
        val invokeParts = SmartReflektInvokeParts.parse(expressionFqName) ?: return null
        messageCollector?.log("SMART REFLEKT CALL: $expressionFqName;")

        // Parse SmartReflekt call to find arguments again
        val invokeArguments = findSmartReflektInvokeArgumentsByExpressionPart(expression, binding)!!

        // Get instances stored in binding context after analysis part
        val instances = c.codegen.bindingContext.getInstances() ?: throw ReflektGenerationException("Found call to Reflekt instances, but no analysis data")

        val resultValues = when (invokeParts.entityType) {
            ReflektEntity.CLASSES -> {
                val classInstances = instances.classes
                filterClassOrObjectInstances(classInstances, invokeArguments, c).map { it.genAsmType(c) }
            }
            ReflektEntity.OBJECTS -> {
                val objectInstances = instances.objects
                filterClassOrObjectInstances(objectInstances, invokeArguments, c).map { it.genAsmType(c) }
            }
            ReflektEntity.FUNCTIONS -> {
                val functionInstances = instances.functions
                filterInstances(functionInstances.filter { it.checkSignature(invokeArguments.supertype!!, binding) }, invokeArguments)
                    .map { it.genAsmType(c, functionInstanceGenerator) }
            }
        }

        return pushResult(resolvedCall, c, invokeParts, resultValues)
    }

    // Filters list of instances (KtObjectDeclaration/KtClass/KtNamedFunction) so that its type matches specified type
    // and each of predicates returns true.
    private inline fun <reified T> filterInstances(instances: List<T>, invokeArguments: SupertypesToFilters): List<T> {
        val imports = importChecker.filterImports(invokeArguments.imports)

        val resultInstances = ArrayList<T>()
        for (instance in instances) {
            var matches = true
            for (filter in invokeArguments.filters) {
                val result = KotlinScript(
                    classpath = classpath,
                    imports = imports,
                    properties = filter.parameters.zip(listOf(T::class)),
                    code = filter.body
                ).eval(listOf(instance)) as Boolean
                if (!result) {
                    matches = false
                }
                if (!matches) {
                    break
                }
            }
            if (matches) {
                resultInstances.push(instance)
            }
        }
        return resultInstances
    }

    private inline fun <reified T: KtClassOrObject> filterClassOrObjectInstances(
        instances: List<T>, invokeArguments: SupertypesToFilters, c: ExpressionCodegenExtension.Context
    ): List<T> =
        filterInstances(instances.filter { it.isSubtypeOf(setOfNotNull(invokeArguments.supertype?.fqName()), c.codegen.bindingContext) }, invokeArguments)
}
