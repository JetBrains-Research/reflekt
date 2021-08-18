package io.reflekt.plugin.generation.bytecode

import io.reflekt.plugin.analysis.common.*
import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.generation.bytecode.util.genAsmType
import io.reflekt.plugin.generation.common.ReflektGenerationException
import io.reflekt.plugin.generation.common.ReflektInvokeParts
import io.reflekt.plugin.utils.Util.getUses
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type

class ReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : BaseReflektGeneratorExtension() {

    override fun myApplyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        val binding = c.codegen.bindingContext
        val expression = resolvedCall.call.calleeExpression ?: return null

        val expressionFqName = expression.getFqName(c.codegen.bindingContext) ?: return null

        // Split expression into known parts of Reflekt invoke
        val invokeParts = ReflektInvokeParts.parse(expressionFqName) ?: return null
        messageCollector?.log("REFLEKT CALL: $expressionFqName;")

        // Get ReflektUses stored in binding context after analysis part
        val uses = c.codegen.bindingContext.getUses() ?: throw ReflektGenerationException("Found call to Reflekt, but no analysis data")

        // Extract answer from uses
        val resultValues = when (invokeParts.entityType) {
            ReflektEntity.CLASSES, ReflektEntity.OBJECTS -> {
                val invokeArguments = findReflektInvokeArgumentsByExpressionPart(expression, binding)!!
                invokeParts.getClassOrObjectUses(uses, invokeArguments, c)
            }
            ReflektEntity.FUNCTIONS -> {
                val invokeArguments = findReflektFunctionInvokeArgumentsByExpressionPart(expression, binding)!!
                invokeParts.getFunctionUses(uses, invokeArguments, c, functionInstanceGenerator)
            }
        }

        return pushResult(resolvedCall, c, invokeParts, resultValues)
    }
}

private fun <K, V> ReflektInvokeParts.getUses(items: TypeUses<K, V>, transform: (V) -> Type, invokeArguments: K): List<Type> =
    items[invokeArguments]?.map { transform(it) } ?: throw ReflektGenerationException("No data for call [$this]")

private fun ReflektInvokeParts.getClassOrObjectUses(
    uses: ReflektUses,
    invokeArguments: SupertypesToAnnotations,
    context: ExpressionCodegenExtension.Context
): List<Type> =
    getUses(if (entityType == ReflektEntity.OBJECTS) uses.objects else uses.classes, { it.genAsmType(context) }, invokeArguments)

private fun ReflektInvokeParts.getFunctionUses(
    uses: ReflektUses,
    invokeArguments: SignatureToAnnotations,
    context: ExpressionCodegenExtension.Context,
    functionInstanceGenerator: FunctionInstanceGenerator
): List<Type> =
    getUses(uses.functions, { item: KtNamedFunction -> item.genAsmType(context, functionInstanceGenerator) }, invokeArguments)
