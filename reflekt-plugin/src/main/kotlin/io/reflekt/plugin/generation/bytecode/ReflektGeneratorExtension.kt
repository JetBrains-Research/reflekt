package io.reflekt.plugin.generation.bytecode

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.common.*
import io.reflekt.plugin.analysis.models.ReflektUses
import io.reflekt.plugin.analysis.models.SignatureToAnnotations
import io.reflekt.plugin.analysis.models.SubTypesToAnnotations
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.generation.bytecode.util.pushArray
import io.reflekt.plugin.utils.Util.getUses
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.toEnum
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.binding.CodegenBinding.ASM_TYPE
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type

class ReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : BaseReflektGeneratorExtension() {

    override fun myApplyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        val binding = c.codegen.bindingContext
        val expression = resolvedCall.call.calleeExpression ?: return null

        val expressionFqName = expression.getFqName(c.codegen.bindingContext) ?: return null

        // Split expression into known parts of Reflekt invoke
        val invokeParts = parseReflektInvoke(expressionFqName, Reflekt::class.qualifiedName!!) ?: return null
        messageCollector?.log("REFLEKT CALL: $expressionFqName;")

        // Get ReflektUses stored in binding context after analysis part
        val uses = c.codegen.bindingContext.getUses() ?: throw ReflektGenerationException("Found call to Reflekt, but no analysis data")

        // Extract answer from uses
        val resultValues = when (invokeParts.name) {
            ReflektName.CLASSES, ReflektName.OBJECTS -> {
                val invokeArguments = findReflektInvokeArgumentsByExpressionPart(expression, binding)!!
                invokeParts.getUses(uses, invokeArguments, c)
            }
            ReflektName.FUNCTIONS -> {
                val invokeArguments = findReflektFunctionInvokeArgumentsByExpressionPart(expression, binding)!!
                invokeParts.getUses(uses, invokeArguments, c, functionInstanceGenerator)
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
}

private fun parseReflektInvoke(fqName: String, reflektFqName: String): ReflektInvokeParts? {
    val matchResult = getReflektFullNameRegex(reflektFqName).matchEntire(fqName) ?: return null
    val (_, klass, nestedClass, terminalFunction) = matchResult.groupValues
    return ReflektInvokeParts(
        klass.toEnum(ReflektName.values(), ReflektName::className),
        nestedClass.toEnum(ReflektNestedName.values(), ReflektNestedName::className),
        terminalFunction.toEnum(ReflektTerminalFunctionName.values(), ReflektTerminalFunctionName::functionName)
    )
}

/*
 * Any Reflekt invoke as an expression looks like this:
 * [1]...Reflekt.[2]|Classes/Objects/Functions|.[3]|WithSubtypes/WithAnnotations|.[4]|toList/toSet/etc|
 * If it does not end with terminal function (like toList), we skip it.
 */
internal data class ReflektInvokeParts(
    override val name: ReflektName,
    override val nestedName: ReflektNestedName,
    override val terminalFunctionName: ReflektTerminalFunctionName
) : BaseReflektInvokeParts(name, nestedName, terminalFunctionName) {
    // Extract result classes or objects and convert them into ASM types.
    fun getUses(
        uses: ReflektUses,
        invokeArguments: SubTypesToAnnotations,
        c: ExpressionCodegenExtension.Context
    ): List<Type> {
        val binding = c.codegen.bindingContext
        val items = if (name == ReflektName.OBJECTS) uses.objects else uses.classes
        return items[invokeArguments]?.map {
            binding.get(ASM_TYPE, it.findClassDescriptor(binding)) ?: throw ReflektGenerationException("Failed to resolve class [$it]")
        } ?: throw ReflektGenerationException("No data for call [$this]")
    }

    // Extract result functions and convert them into ASM types.
    fun getUses(
        uses: ReflektUses,
        invokeArguments: SignatureToAnnotations,
        c: ExpressionCodegenExtension.Context,
        functionInstanceGenerator: FunctionInstanceGenerator
    ): List<Type> {
        val items = uses.functions
        return items[invokeArguments]?.map {
            functionInstanceGenerator.generate(it, c)
        } ?: throw ReflektGenerationException("No data for call [$this]")
    }
}
