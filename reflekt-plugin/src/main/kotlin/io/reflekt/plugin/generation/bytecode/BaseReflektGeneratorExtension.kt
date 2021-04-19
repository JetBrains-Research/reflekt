package io.reflekt.plugin.generation.bytecode

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.generation.bytecode.util.pushArray
import io.reflekt.plugin.generation.bytecode.util.pushFunctionN
import io.reflekt.plugin.generation.bytecode.util.pushKClass
import io.reflekt.plugin.generation.bytecode.util.pushObject
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

open class BaseReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : ExpressionCodegenExtension {
    protected open val functionInstanceGenerator = FunctionInstanceGenerator("io/reflekt/generated/Functions", messageCollector)

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

    internal fun pushResult(
        resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context,
        invokeParts: BaseReflektInvokeParts, resultValues: List<Type>
    ): StackValue? {
        // Return type (e.g. List or Set)
        val returnType = resolvedCall.candidateDescriptor.returnType ?: error("No return type info")
        // Type of each answer (e.g KClass or Function2)
        val returnTypeArgument = returnType.arguments.first().type.asmType(c.typeMapper)

        return StackValue.functionCall(returnType.asmType(c.typeMapper), null) {
            it.pushArray(returnTypeArgument, resultValues, invokeParts.pushItemFunction)
            invokeParts.invokeTerminalFunction(it)
        }
    }
}

/*
 * Any Reflekt invoke as an expression looks like this:
 * [1]...Reflekt.[2]|Classes/Objects/Functions|.[3]|nested function|.[4]|toList/toSet/etc|
 * If it does not end with terminal function (like toList), we skip it.
 */
internal abstract class BaseReflektInvokeParts(
    open val entityType: ReflektEntity
) {
    // Push a value of specified type on stack depending on which kind it is.
    val pushItemFunction: InstructionAdapter.(Type) -> Unit
        get() = when (entityType) {
            ReflektEntity.OBJECTS -> InstructionAdapter::pushObject
            ReflektEntity.CLASSES -> InstructionAdapter::pushKClass
            ReflektEntity.FUNCTIONS -> InstructionAdapter::pushFunctionN
        }
    // Invoke terminal function after preparing arguments.
    abstract val invokeTerminalFunction: InstructionAdapter.() -> Unit
}

