package io.reflekt.plugin.generation.bytecode

import io.reflekt.SmartReflekt
import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.common.SmartReflektTerminalFunction
import io.reflekt.plugin.analysis.common.findSmartReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.models.SubTypesToFilters
import io.reflekt.plugin.analysis.psi.function.checkSignature
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import io.reflekt.plugin.generation.bytecode.util.genAsmType
import io.reflekt.plugin.generation.bytecode.util.invokeListOf
import io.reflekt.plugin.generation.bytecode.util.pushArray
import io.reflekt.plugin.scripting.ImportChecker
import io.reflekt.plugin.scripting.KotlinScriptRunner
import io.reflekt.plugin.utils.Util.getInstances
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.enumToRegexOptions
import io.reflekt.plugin.utils.toEnum
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
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
        val invokeParts = parseSmartReflektInvoke(expressionFqName) ?: return null
        messageCollector?.log("SMART REFLEKT CALL: $expressionFqName;")

        // Parse SmartReflekt call to find arguments again
        val invokeArguments = findSmartReflektInvokeArgumentsByExpressionPart(expression, binding)!!

        // Get instances stored in binding context after analysis part
        val instances = c.codegen.bindingContext.getInstances() ?: throw ReflektGenerationException("Found call to Reflekt instances, but no analysis data")

        val resultValues = when (invokeParts.entityType) {
            ReflektEntity.CLASSES -> {
                val classInstances= instances.classes
                filterInstances(classInstances.filter { it.isSubtypeOf(setOfNotNull(invokeArguments.subType?.fqName), binding) }, invokeArguments)
                    .map { it.genAsmType(c) }
            }
            ReflektEntity.OBJECTS -> {
                val objectInstances= instances.objects
                filterInstances(objectInstances.filter { it.isSubtypeOf(setOfNotNull(invokeArguments.subType?.fqName), binding) }, invokeArguments)
                    .map { it.genAsmType(c) }
            }
            ReflektEntity.FUNCTIONS -> {
                val functionInstances = instances.functions
                filterInstances(functionInstances.filter { it.checkSignature(invokeArguments.subType!!, binding) }, invokeArguments)
                    .map { it.genAsmType(c, functionInstanceGenerator) }
            }
        }

        // Return type (e.g. List or Set)
        val returnType = resolvedCall.candidateDescriptor.returnType!!
        // Type of each answer (e.g KClass or Function2)
        val returnTypeArgument = returnType.arguments.first().type.asmType(c.typeMapper)

        return StackValue.functionCall(returnType.asmType(c.typeMapper), null) {
            it.pushArray(returnTypeArgument, resultValues, invokeParts.pushItemFunction)
            invokeParts.invokeTerminalFunction(it)
        }
    }

    private inline fun <reified T> filterInstances(instances: List<T>, invokeArguments: SubTypesToFilters): List<T> {
        val resultInstances = ArrayList<T>()
        for (instance in instances) {
            var matches = true
            for (filter in invokeArguments.filters) {
                with(KotlinScriptRunner(classpath)) {
                    addImports(importChecker.filterImports(invokeArguments.imports))
                    filter.parameters.forEach {
                        addValue(it, instance)
                    }
                    if (eval(filter.body) == false) {
                        matches = false
                    }
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
}

private fun getSmartReflektFullNameRegex(): Regex {
    val smartReflektFqName = SmartReflekt::class.qualifiedName!!
    val entityClasses = enumToRegexOptions(ReflektEntity.values(), ReflektEntity::smartClassName)
    val terminalFunctions = enumToRegexOptions(SmartReflektTerminalFunction.values(), SmartReflektTerminalFunction::functionName)
    return Regex("$smartReflektFqName\\.$entityClasses\\.$terminalFunctions")
}

private fun parseSmartReflektInvoke(fqName: String): SmartReflektInvokeParts? {
    val matchResult = getSmartReflektFullNameRegex().matchEntire(fqName) ?: return null
    val (_, entityClass, terminalFunction) = matchResult.groupValues
    return SmartReflektInvokeParts(
        entityClass.toEnum(ReflektEntity.values(), ReflektEntity::smartClassName),
        terminalFunction.toEnum(SmartReflektTerminalFunction.values(), SmartReflektTerminalFunction::functionName)
    )
}

/*
 * Any SmartReflekt invoke as an expression looks like this:
 * [1]...Reflekt.[2]|Classes/Objects/Functions|.[3]|ClassCompileTimeExpression/ObjectCompileTimeExpression/FunctionCompileTimeExpression|.[4]|resolve/etc|
 * If it does not end with terminal function (like resolve), we skip it.
 */
internal data class SmartReflektInvokeParts(
    override val entityType: ReflektEntity,
    val terminalFunction: SmartReflektTerminalFunction
) : BaseReflektInvokeParts(entityType) {
    // Invoke terminal function after preparing arguments.
    val invokeTerminalFunction: InstructionAdapter.() -> Unit
        get() = when (terminalFunction) {
            SmartReflektTerminalFunction.RESOLVE -> InstructionAdapter::invokeListOf
        }
}
