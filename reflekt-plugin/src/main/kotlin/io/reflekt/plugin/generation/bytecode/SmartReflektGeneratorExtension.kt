package io.reflekt.plugin.generation.bytecode

import io.reflekt.SmartReflekt
import io.reflekt.plugin.analysis.common.*
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.utils.Util.getInstances
import io.reflekt.plugin.utils.Util.getUses
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class SmartReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : BaseReflektGeneratorExtension() {

    override fun myApplyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        val binding = c.codegen.bindingContext
        val expression = resolvedCall.call.calleeExpression ?: return null

        val expressionFqName = expression.getFqName(c.codegen.bindingContext) ?: return null

        // Split expression into known parts of SmartReflekt invoke
        val invokeParts = parseReflektInvoke(expressionFqName, SmartReflekt::class.qualifiedName!!) ?: return null
        messageCollector?.log("SMART REFLEKT CALL: $expressionFqName;")

        // Parse SmartReflekt call to find arguments again
        val invokeArguments = findSmartReflektInvokeArgumentsByExpressionPart(expression, binding)!!

        // Get instances stored in binding context after analysis part
        val instances = c.codegen.bindingContext.getInstances() ?: throw ReflektGenerationException("Found call to Reflekt instances, but no analysis data")

        TODO("Filter instances according to invokeArguments and generate bytecode")
    }
}

// TODO: create a fabric to get BaseReflektInvokeParts
private fun parseReflektInvoke(fqName: String, reflektFqName: String): SmartReflektInvokeParts? {
    val matchResult = getReflektFullNameRegex(reflektFqName).matchEntire(fqName) ?: return null
    val (_, klass, nestedClass, terminalFunction) = matchResult.groupValues
    return SmartReflektInvokeParts(
        klass.toEnum(ReflektName.values(), ReflektName::className)!!,
        nestedClass.toEnum(ReflektNestedName.values(), ReflektNestedName::className)!!,
        terminalFunction.toEnum(ReflektTerminalFunctionName.values(), ReflektTerminalFunctionName::functionName)!!
    )
}

/*
 * Any SmartReflekt invoke as an expression looks like this:
 * [1]...Reflekt.[2]|Classes/Objects/Functions|.[3]|ClassCompileTimeExpression/ObjectCompileTimeExpression/FunctionCompileTimeExpression|.[4]|resolve/etc|
 * If it does not end with terminal function (like resolve), we skip it.
 */
internal data class SmartReflektInvokeParts(
    override val name: ReflektName,
    override val nestedName: ReflektNestedName,
    override val terminalFunctionName: ReflektTerminalFunctionName
) : BaseReflektInvokeParts(name, nestedName, terminalFunctionName)
