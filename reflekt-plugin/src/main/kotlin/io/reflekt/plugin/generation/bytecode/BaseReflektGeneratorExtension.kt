package io.reflekt.plugin.generation.bytecode

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.common.ReflektName
import io.reflekt.plugin.analysis.common.ReflektNestedName
import io.reflekt.plugin.analysis.common.ReflektTerminalFunctionName
import io.reflekt.plugin.generation.bytecode.util.*
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

open class BaseReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : ExpressionCodegenExtension {
    protected val functionInstanceGenerator = FunctionInstanceGenerator("io/reflekt/generated/Functions", messageCollector)

    open fun myApplyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        TODO("Not impemented yet")
    }

    override fun applyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        try {
            return myApplyFunction(receiver, resolvedCall, c)
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

/*
 * Any Reflekt invoke as an expression looks like this:
 * [1]...Reflekt.[2]|Classes/Objects/Functions|.[3]|nested function|.[4]|toList/toSet/etc|
 * If it does not end with terminal function (like toList), we skip it.
 */
internal abstract class BaseReflektInvokeParts(
    open val name: ReflektName,
    open val nestedName: ReflektNestedName,
    open val terminalFunctionName: ReflektTerminalFunctionName
) {
    // Push a value of specified type on stack depending on which kind it is.
    val pushItemFunction: InstructionAdapter.(Type) -> Unit
        get() = when (name) {
            ReflektName.OBJECTS -> InstructionAdapter::pushObject
            ReflektName.CLASSES -> InstructionAdapter::pushKClass
            ReflektName.FUNCTIONS -> InstructionAdapter::pushFunctionN
        }

    // Invoke terminal function after preparing arguments.
    val invokeTerminalFunction: InstructionAdapter.() -> Unit
        get() = when (terminalFunctionName) {
            ReflektTerminalFunctionName.TO_LIST, ReflektTerminalFunctionName.RESOLVE -> InstructionAdapter::invokeListOf
            ReflektTerminalFunctionName.TO_SET -> InstructionAdapter::invokeSetOf
        }
}

private fun <T : Enum<T>> enumToRegexOptions(values: Array<T>, transform: T.() -> String): String =
    "(${values.joinToString(separator = "|") { it.transform() }})"

internal fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T? =
    values.first { it.transform() == this }

internal fun getReflektFullNameRegex(reflektFqName: String): Regex {
    val names = enumToRegexOptions(ReflektName.values(), ReflektName::className)
    val nestedNames = enumToRegexOptions(ReflektNestedName.values(), ReflektNestedName::className)
    val terminalNames = enumToRegexOptions(ReflektTerminalFunctionName.values(), ReflektTerminalFunctionName::functionName)
    return Regex("$reflektFqName\\.$names\\.$nestedNames\\.$terminalNames")
}
