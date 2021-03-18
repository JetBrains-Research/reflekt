package io.reflekt.plugin.generation.bytecode

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.common.*
import io.reflekt.plugin.analysis.models.ReflektUses
import io.reflekt.plugin.analysis.models.SignatureToAnnotations
import io.reflekt.plugin.analysis.models.SubTypesToAnnotations
import io.reflekt.plugin.analysis.models.TypeUses
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.generation.bytecode.util.genAsmType
import io.reflekt.plugin.generation.bytecode.util.invokeListOf
import io.reflekt.plugin.generation.bytecode.util.invokeSetOf
import io.reflekt.plugin.generation.bytecode.util.pushArray
import io.reflekt.plugin.utils.Util.getUses
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.enumToRegexOptions
import io.reflekt.plugin.utils.toEnum
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class ReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : BaseReflektGeneratorExtension() {

    override fun myApplyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        val binding = c.codegen.bindingContext
        val expression = resolvedCall.call.calleeExpression ?: return null

        val expressionFqName = expression.getFqName(c.codegen.bindingContext) ?: return null

        // Split expression into known parts of Reflekt invoke
        val invokeParts = parseReflektInvoke(expressionFqName) ?: return null
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

private fun getReflektFullNameRegex(): Regex {
    val reflektFqName = Reflekt::class.qualifiedName!!
    val entityTypes = enumToRegexOptions(ReflektEntity.values(), ReflektEntity::className)
    val nestedClasses = enumToRegexOptions(ReflektNestedClass.values(), ReflektNestedClass::className)
    val terminalFunctions = enumToRegexOptions(ReflektTerminalFunction.values(), ReflektTerminalFunction::functionName)
    return Regex("$reflektFqName\\.$entityTypes\\.$nestedClasses\\.$terminalFunctions")
}

private fun parseReflektInvoke(fqName: String): ReflektInvokeParts? {
    val matchResult = getReflektFullNameRegex().matchEntire(fqName) ?: return null
    val (_, klass, nestedClass, terminalFunction) = matchResult.groupValues
    return ReflektInvokeParts(
        klass.toEnum(ReflektEntity.values(), ReflektEntity::className),
        nestedClass.toEnum(ReflektNestedClass.values(), ReflektNestedClass::className),
        terminalFunction.toEnum(ReflektTerminalFunction.values(), ReflektTerminalFunction::functionName)
    )
}

/*
 * Any Reflekt invoke as an expression looks like this:
 * [1]...Reflekt.[2]|Classes/Objects/Functions|.[3]|WithSubtypes/WithAnnotations|.[4]|toList/toSet/etc|
 * If it does not end with terminal function (like toList), we skip it.
 */
internal data class ReflektInvokeParts(
    override val entityType: ReflektEntity,
    val nestedClass: ReflektNestedClass,
    val terminalFunction: ReflektTerminalFunction
) : BaseReflektInvokeParts(entityType) {
    // Invoke terminal function after preparing arguments.
    val invokeTerminalFunction: InstructionAdapter.() -> Unit
        get() = when (terminalFunction) {
            ReflektTerminalFunction.TO_LIST -> InstructionAdapter::invokeListOf
            ReflektTerminalFunction.TO_SET -> InstructionAdapter::invokeSetOf
        }

    fun <K, V> getUses(items: TypeUses<K, V>, transform: (V) -> Type, invokeArguments: K): List<Type> =
        items[invokeArguments]?.map { transform(it) } ?: throw ReflektGenerationException("No data for call [$this]")

    fun getClassOrObjectUses(
        uses: ReflektUses,
        invokeArguments: SubTypesToAnnotations,
        context: ExpressionCodegenExtension.Context
    ): List<Type> =
        getUses(if (entityType == ReflektEntity.OBJECTS) uses.objects else uses.classes, { it.genAsmType(context) }, invokeArguments)

    fun getFunctionUses(
        uses: ReflektUses,
        invokeArguments: SignatureToAnnotations,
        context: ExpressionCodegenExtension.Context,
        functionInstanceGenerator: FunctionInstanceGenerator
    ): List<Type> =
        getUses(uses.functions, { item: KtNamedFunction -> item.genAsmType(context, functionInstanceGenerator) }, invokeArguments)
}
