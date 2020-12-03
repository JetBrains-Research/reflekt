package io.reflekt.plugin.generation.bytecode

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.ReflektUses
import io.reflekt.plugin.analysis.SubTypesToAnnotations
import io.reflekt.plugin.analysis.common.ReflektName
import io.reflekt.plugin.analysis.common.ReflektNestedName
import io.reflekt.plugin.analysis.common.ReflektTerminalFunctionName
import io.reflekt.plugin.analysis.common.findReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.generation.bytecode.util.*
import io.reflekt.plugin.utils.Util.getUses
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.binding.CodegenBinding.ASM_TYPE
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class ReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : ExpressionCodegenExtension {
    private val functionInstanceGenerator = FunctionInstanceGenerator("io/reflekt/generated/Functions", messageCollector)

    override fun applyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        try {
            val binding = c.codegen.bindingContext
            val expression = resolvedCall.call.calleeExpression ?: return null

            val invokeNames = parseReflektInvoke(expression.getFqName(c.codegen.bindingContext) ?: return null) ?: return null
            val invokeArguments = findReflektInvokeArgumentsByExpressionPart(expression, binding)!!

            val uses = c.codegen.bindingContext.getUses() ?: throw ReflektGenerationException("Found call to Reflekt, but no analysis data")
            val resultValues = invokeNames.getUses(uses, invokeArguments, c, functionInstanceGenerator)

            val returnType = resolvedCall.candidateDescriptor.returnType!!
            val returnTypeArgument = returnType.arguments.first().type.asmType(c.typeMapper)

            return StackValue.functionCall(returnType.asmType(c.typeMapper), null) {
                it.pushArray(returnTypeArgument, resultValues, invokeNames.pushItemFunction)
                invokeNames.invokeTerminalFunction(it)
            }
        } catch (e: Exception) {
            val reflektGenerationException = ReflektGenerationException(
                message = "Failed to generate Reflekt bytecode implementation: ${e.message}",
                cause = e
            )
            messageCollector?.log("ERROR: $reflektGenerationException;")
            throw reflektGenerationException
        }
    }
}

private data class ReflektInvokeNames(
    val name: ReflektName,
    val nestedName: ReflektNestedName,
    val terminalFunctionName: ReflektTerminalFunctionName
) {
    val pushItemFunction: InstructionAdapter.(Type) -> Unit
        get() = when (name) {
            ReflektName.OBJECTS -> InstructionAdapter::pushObject
            ReflektName.CLASSES -> InstructionAdapter::pushKClass
            ReflektName.FUNCTIONS -> InstructionAdapter::pushFunctionN
        }
    val invokeTerminalFunction: InstructionAdapter.() -> Unit
        get() = when (terminalFunctionName) {
            ReflektTerminalFunctionName.TO_LIST -> InstructionAdapter::invokeListOf
            ReflektTerminalFunctionName.TO_SET -> InstructionAdapter::invokeSetOf
        }

    fun getUses(
        uses: ReflektUses,
        invokeArguments: SubTypesToAnnotations,
        c: ExpressionCodegenExtension.Context,
        functionInstanceGenerator: FunctionInstanceGenerator
    ): List<Type> {
        val binding = c.codegen.bindingContext
        return when (val type = name) {
            ReflektName.OBJECTS, ReflektName.CLASSES -> {
                val items = if (type == ReflektName.OBJECTS) uses.objects else uses.classes
                items[invokeArguments]?.map {
                    binding.get(ASM_TYPE, it.findClassDescriptor(binding)) ?: throw ReflektGenerationException("Failed to resolve class [$it]")
                }
            }
            ReflektName.FUNCTIONS -> {
                val items = uses.functions
                items[invokeArguments.annotations]?.map {
                    functionInstanceGenerator.generate(it, c)
                }
            }
        } ?: throw ReflektGenerationException("No data for call [$this]")
    }
}

private fun <T : Enum<T>> enumToRegexOptions(values: Array<T>, transform: T.() -> String): String =
    "(${values.joinToString(separator = "|") { it.transform() }})"

private fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T? =
    values.first { it.transform() == this }

private fun parseReflektInvoke(fqName: String): ReflektInvokeNames? {
    val names = enumToRegexOptions(ReflektName.values(), ReflektName::className)
    val nestedNames = enumToRegexOptions(ReflektNestedName.values(), ReflektNestedName::className)
    val terminalNames = enumToRegexOptions(ReflektTerminalFunctionName.values(), ReflektTerminalFunctionName::functionName)
    val regex = Regex("${Reflekt::class.qualifiedName}\\.$names\\.$nestedNames\\.$terminalNames")

    val matchResult = regex.matchEntire(fqName) ?: return null
    val (_, klass, nestedClass, terminalFunction) = matchResult.groupValues
    return ReflektInvokeNames(
        klass.toEnum(ReflektName.values(), ReflektName::className)!!,
        nestedClass.toEnum(ReflektNestedName.values(), ReflektNestedName::className)!!,
        terminalFunction.toEnum(ReflektTerminalFunctionName.values(), ReflektTerminalFunctionName::functionName)!!
    )
}
