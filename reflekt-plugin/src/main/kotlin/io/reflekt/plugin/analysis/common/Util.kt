package io.reflekt.plugin.analysis.common

import io.reflekt.plugin.analysis.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext

fun findReflektInvokeArguments(dotQualifiedExpressionNode: ASTNode, binding: BindingContext): SubTypesToAnnotations? {
    /*
     * Any Reflekt invoke is something like this: ... [1]Reflekt.[2]|objects()/classes() or so on|....
     * We can find the [2] place by fqName (it is KtReferenceExpression)
     * To find the [1] place we should go to the 1 level above from [2]
     * and find the root of the nested DOT_QUALIFIED_EXPRESSION nodes
     */

    val filteredChildren = dotQualifiedExpressionNode.filterChildren { n: ASTNode -> n.text in ReflektFunctionName.values().map { it.functionName } }

    val subtypes = HashSet<String>()
    val annotations = HashSet<String>()

    for (node in filteredChildren) {
        val callExpressionRoot = node.parents().firstOrNull { it.elementType.toString() == ElementType.CallExpression.value } ?: continue
        when(node.text) {
            ReflektFunctionName.WITH_SUBTYPE.functionName -> callExpressionRoot.getFqNamesOfTypeArgument(binding).let { subtypes.addAll(it) }
            ReflektFunctionName.WITH_SUBTYPES.functionName -> callExpressionRoot.getFqNamesOfValueArguments(binding).let { subtypes.addAll(it) }
            ReflektFunctionName.WITH_ANNOTATIONS.functionName -> {
                callExpressionRoot.getFqNamesOfTypeArgument(binding).let { subtypes.addAll(it) }
                callExpressionRoot.getFqNamesOfValueArguments(binding).let { annotations.addAll(it) }
            }
            else -> error("Found an unexpected node text: ${node.text}")
        }
    }
    if (subtypes.isEmpty()) {
        return null
    }
    return SubTypesToAnnotations(subtypes, annotations)
}

fun findReflektInvokeArgumentsByExpressionPart(expression: KtExpression, binding: BindingContext): SubTypesToAnnotations? {
    val callExpressionRoot = expression.node.parents().first()
    return callExpressionRoot.findLastParentByType(ElementType.DotQualifiedExpression)?.let { node ->
        findReflektInvokeArguments(node, binding)
    }
}
