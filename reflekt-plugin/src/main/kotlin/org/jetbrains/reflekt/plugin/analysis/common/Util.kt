package org.jetbrains.reflekt.plugin.analysis.common

import org.jetbrains.reflekt.plugin.analysis.*
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.psi.*

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

private fun ASTNode.findMatchingFunctionInExpression() = this.filterChildren { node -> node.text in ReflektFunction.values().map { it.functionName } }

// [1]Reflekt.[2]|objects()/classes() or so on|
// [dotQualifiedExpressionNode] is [1]
fun findReflektInvokeArguments(dotQualifiedExpressionNode: ASTNode, binding: BindingContext): SupertypesToAnnotations? {
    val filteredChildren = dotQualifiedExpressionNode.findMatchingFunctionInExpression()

    val supertypes = HashSet<String>()
    val annotations = HashSet<String>()

    for (node in filteredChildren) {
        val callExpressionRoot = node.parents().firstOrNull { it.hasType(ElementType.CALL_EXPRESSION) } ?: continue
        when (node.text) {
            ReflektFunction.WITH_SUPERTYPE.functionName -> callExpressionRoot.getFqNamesOfTypeArgument(binding).let { supertypes.addAll(it) }
            ReflektFunction.WITH_SUPERTYPES.functionName -> callExpressionRoot.getFqNamesOfValueArguments(binding).let { supertypes.addAll(it) }
            ReflektFunction.WITH_ANNOTATIONS.functionName -> {
                callExpressionRoot.getFqNamesOfTypeArgument(binding).let { supertypes.addAll(it) }
                callExpressionRoot.getFqNamesOfValueArguments(binding).let { annotations.addAll(it) }
            }
            else -> error("Found an unexpected node text: ${node.text}")
        }
    }
    if (supertypes.isEmpty()) {
        return null
    }
    return SupertypesToAnnotations(supertypes, annotations)
}

fun findReflektInvokeArgumentsByExpressionPart(expression: KtExpression, binding: BindingContext): SupertypesToAnnotations? {
    /**
     * We use this function only for REFERENCE_EXPRESSION nodes. Any vertex of this type has the following structure:
     * CALL_EXPRESSION -> REFERENCE_EXPRESSION
     * We want to get the root of this expression (CALL_EXPRESSION)
     * For example, in our case we have the following expression: Reflekt.objects()
     * The root of objects() part is CALL_EXPRESSION
     */
    val callExpressionRoot = expression.node.parents().first()

    /**
     * Any Reflekt invoke is something like this: ... [1]Reflekt.[2]|objects()/classes() or so on|....
     * We can find the [2] - callExpressionRoot
     * To find the [1] place we should go to the 1 level above from [2]
     * by finding the root of the nested DOT_QUALIFIED_EXPRESSION nodes
     */
    return callExpressionRoot.findLastParentByType(ElementType.DOT_QUALIFIED_EXPRESSION)?.let { node ->
        findReflektInvokeArguments(node, binding)
    }
}

// [1]SmartReflekt.[2]|objects()/classes() or so on|
// [dotQualifiedExpressionNode] is [1]
fun findSmartReflektInvokeArguments(dotQualifiedExpressionNode: ASTNode, binding: BindingContext): SupertypesToFilters? {
    val filteredChildren = dotQualifiedExpressionNode.filterChildren { node ->
        (node.text in SmartReflektFunction.values().map { it.functionName } || node.text in ReflektEntity.values().map { it.entityType }) &&
            node.hasType(ElementType.REFERENCE_EXPRESSION)
    }
    var supertype: KotlinType? = null
    val filters = ArrayList<Lambda>()
    for (node in filteredChildren) {
        val childCallExpressionRoot = node.parents().firstOrNull { it.elementType.toString() == ElementType.CALL_EXPRESSION.value } ?: continue
        when (node.text) {
            SmartReflektFunction.FILTER.functionName -> {
                val body = childCallExpressionRoot.getLambdaBody()
                val parameters = childCallExpressionRoot.getLambdaParameters()
                filters.add(Lambda(body, parameters))
            }
            in ReflektEntity.values().map { it.entityType } -> supertype = childCallExpressionRoot.getTypeArguments().first().toParameterizedType(binding)
            else -> error("Found an unexpected node text: ${node.text}")
        }
    }
    if (filters.isEmpty()) {
        return null
    }
    val imports = (dotQualifiedExpressionNode.parents().first { it.hasType(ElementType.FILE) }.psi as KtFile).importDirectives.map {
        Import(it.importedFqName.toString(), it.text)
    }

    return SupertypesToFilters(supertype, filters, imports)
}

fun findSmartReflektInvokeArgumentsByExpressionPart(expression: KtExpression, binding: BindingContext): SupertypesToFilters? {
    val callExpressionRoot = expression.node.parents().first()
    return callExpressionRoot.findLastParentByType(ElementType.DOT_QUALIFIED_EXPRESSION)?.let { node ->
        findSmartReflektInvokeArguments(node, binding)
    }
}
