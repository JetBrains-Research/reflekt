package io.reflekt.plugin.generation

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.ReflektUses
import io.reflekt.plugin.analysis.SubTypesToAnnotations
import io.reflekt.plugin.analysis.common.ReflektName
import io.reflekt.plugin.analysis.common.ReflektNestedName
import io.reflekt.plugin.analysis.common.ReflektTerminalFunctionName
import io.reflekt.plugin.analysis.common.findReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.utils.Util.getUses
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.asmType
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.fileClasses.internalNameWithoutInnerClasses
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class ReflektGeneratorExtension(private val messageCollector: MessageCollector? = null) : ExpressionCodegenExtension {
    override fun applyFunction(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context): StackValue? {
        val binding = c.codegen.bindingContext
        val expression = resolvedCall.call.calleeExpression ?: return null
        val invokeNames = parseReflektInvoke(expression.getFqName(c.codegen.bindingContext) ?: return null) ?: return null
        val invokeArguments = findReflektInvokeArgumentsByExpressionPart(expression, binding)!!
        val uses = c.codegen.bindingContext.getUses() ?: return null
        val resultValues = uses.get(invokeNames, invokeArguments) ?: return null

        val returnType = resolvedCall.candidateDescriptor.returnType!!
        val returnTypeArgument = returnType.arguments.first().type.asmType(c.typeMapper)
        return StackValue.functionCall(returnType.asmType(c.typeMapper), null) {
            it.writeResultValues(returnTypeArgument, resultValues, invokeNames)
        }
    }

    private fun InstructionAdapter.writeResultValues(resultType: Type, items: List<Type>, invokeNames: ReflektInvokeNames) {
        iconst(items.size)
        newarray(resultType)
        items.forEachIndexed { index, item ->
            dup()
            iconst(index)
            getItem(item, invokeNames.name)
            checkcast(resultType)
            astore(InstructionAdapter.OBJECT_TYPE)
        }
        invoke(invokeNames.terminalFunctionName)
    }

    private fun InstructionAdapter.invoke(function: ReflektTerminalFunctionName) {
        when (function) {
            ReflektTerminalFunctionName.TO_LIST ->
                invokestatic("kotlin/collections/CollectionsKt", "listOf", "([Ljava/lang/Object;)Ljava/util/List;", false)
            ReflektTerminalFunctionName.TO_SET ->
                invokestatic("kotlin/collections/SetsKt", "setOf", "([Ljava/lang/Object;)Ljava/util/Set;", false)
        }
    }

    private fun InstructionAdapter.getItem(item: Type, type: ReflektName) {
        when (type) {
            ReflektName.OBJECTS -> {
                getstatic(item.internalName, "INSTANCE", item.descriptor)
            }
            ReflektName.CLASSES -> {
                visitLdcInsn(item)
                invokestatic("kotlin/jvm/internal/Reflection", "getOrCreateKotlinClass", "(Ljava/lang/Class;)Lkotlin/reflect/KClass;", false)
            }
            ReflektName.FUNCTIONS -> TODO()
        }
    }
}

private fun ReflektUses.get(invokeNames: ReflektInvokeNames, invokeArguments: SubTypesToAnnotations): List<Type>? {
    return when (val type = invokeNames.name) {
        ReflektName.OBJECTS, ReflektName.CLASSES -> {
            val items = if (type == ReflektName.OBJECTS) objects else classes
            items.getValue(invokeArguments).map {
                Type.getObjectType(it.fqName!!.internalNameWithoutInnerClasses)
            }
        }
        ReflektName.FUNCTIONS -> TODO()
    }
}

data class ReflektInvokeNames(
    val name: ReflektName,
    val nestedName: ReflektNestedName,
    val terminalFunctionName: ReflektTerminalFunctionName
)

private fun <T : Enum<T>> enumToRegexOptions(values: Array<T>, transform: T.() -> String) =
    "(${values.joinToString(separator = "|") { it.transform() }})"

private fun <T : Enum<T>> String.toEnum(values: Array<T>, transform: T.() -> String): T? =
    values.first { it.transform() == this }

private fun parseReflektInvoke(fqName: String): ReflektInvokeNames? {
    val names = enumToRegexOptions(ReflektName.values()) { className }
    val nestedNames = enumToRegexOptions(ReflektNestedName.values()) { className }
    val terminalNames = enumToRegexOptions(ReflektTerminalFunctionName.values()) { functionName }
    val regex = Regex("${Reflekt::class.qualifiedName}\\.$names\\.$nestedNames\\.$terminalNames")

    val matchResult = regex.matchEntire(fqName) ?: return null
    val (_, klass, nestedClass, terminalFunction) = matchResult.groupValues
    return ReflektInvokeNames(
        klass.toEnum(ReflektName.values()) { className }!!,
        nestedClass.toEnum(ReflektNestedName.values()) { className }!!,
        terminalFunction.toEnum(ReflektTerminalFunctionName.values()) { functionName }!!
    )
}
