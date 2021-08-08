package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.ElementType
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.codegen.kotlinType
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getCalleeExpressionIfAny
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import java.util.*


/*
 * Get a list of all parents of nodes and find the last occurrence of a vertex whose type is A ([elementType]).
 * Moreover, all previous vertices also had type A.
 *
 * For example, if we have a list of vertices with types [A, A, A, A, B], and [elementType == A]
 * then we must return index 3, because the last occurrence index is 4 (B != A).
 */
fun ASTNode.findLastParentByType(elementType: ElementType): ASTNode? {
    val parents = this.parents().toList()
    val index = parents.indexOfFirst { !it.hasType(elementType) }
    if (index <= 0) {
        return null
    }
    return parents[index - 1]
}

fun ASTNode.hasType(type: ElementType) = elementType.toString() == type.value

fun ASTNode.getFqNamesOf(rootType: ElementType, type: ElementType, binding: BindingContext): List<String> {
    require(hasType(rootType)) { "Invalid element type ${this.elementType} of the parent node ${this.text}" }
    val typeArgumentNode = this.children().find { it.hasType(type) }!!
    val filtered = typeArgumentNode.filterChildren { it.hasType(ElementType.ReferenceExpression) }.toList()
    return filtered.mapNotNull { it.psi?.getFqName(binding) }
}

fun ASTNode.getFqNamesOfTypeArgument(binding: BindingContext) = getFqNamesOf(ElementType.CallExpression, ElementType.TypeArgumentList, binding)

fun ASTNode.getFqNamesOfValueArguments(binding: BindingContext) = getFqNamesOf(ElementType.CallExpression, ElementType.ValueArgumentList, binding)

fun ASTNode.getLambdaNode(): ASTNode {
    require(hasType(ElementType.CallExpression)) { "Try to get lambda body from the node with type: ${this.elementType}" }
    // CALL_EXPRESSION -> LAMBDA_ARGUMENT -> LAMBDA_EXPRESSION -> FUNCTION_LITERAL -> BLOCK
    // We need to get the last level (BLOCK)
    return this.children().firstOrNull { it.hasType(ElementType.LambdaArgument) }
        ?.children()?.firstOrNull { it.hasType(ElementType.LambdaExpression) }
        ?.children()?.firstOrNull { it.hasType(ElementType.FunctionLiteral) }
        ?: error("Incorrect lambda structure in the CALL_EXPRESSION node")
}

fun ASTNode.getLambdaBody(): String {
    return this.getLambdaNode().children().firstOrNull { it.hasType(ElementType.Block) }
        ?.text ?: error("The lambda node does not have the text attribute")
}

fun ASTNode.getLambdaParameters(): List<String> {
    val parameterList = this.getLambdaNode().children().firstOrNull { it.hasType(ElementType.ValueParameterList) } ?: return listOf("it")
    return parameterList.children().toList().mapNotNull { parameter ->
        parameter.children().firstOrNull { it as? LeafPsiElement != null }?.text
    }
}

/*
 * Traverse all children of the node (use BFS order) and return all children nodes which satisfy the filter condition
 */
fun ASTNode.filterChildren(filter: (node: ASTNode) -> Boolean): Sequence<ASTNode> {
    val filtered = ArrayList<ASTNode>()
    val nodes: Queue<ASTNode> = LinkedList(listOf(this))
    while (nodes.isNotEmpty()) {
        val currentNode = nodes.poll()
        if (filter(currentNode)) {
            filtered.add(currentNode)
        }
        nodes.addAll(currentNode.children())
    }
    return filtered.asSequence()
}

/*
 * Extract list of parameters from ASTNode.
 * For example, CALL_EXPRESSION and USER_TYPE both may have TYPE_ARGUMENT_LIST as the child node.
 * It has the following structure: root -> <list type> -> [<entry type>]
 */
private fun ASTNode.getParameterList(listType: ElementType, entryType: ElementType): List<ASTNode> =
    (children().firstOrNull { it.hasType(listType) }?.children()?.toList() ?: emptyList())
        .filter { it.hasType(entryType) }

/*
 * Get type of parameter.
 * It has the following structure: root -> TYPE_REFERENCE -> USER_TYPE|FUNCTION_TYPE|NULLABLE_TYPE
 */
fun ASTNode.getParameterType(): ASTNode = children().first { it.hasType(ElementType.TypeReference) }.firstChildNode

/*
 * Extract list of types from ASTNode.
 * For example, CALL_EXPRESSION and USER_TYPE both may have TYPE_ARGUMENT_LIST as the child node.
 * It has the following structure: root -> <list type> -> [<entry type>] -> TYPE_REFERENCE -> USER_TYPE|FUNCTION_TYPE
 */
private fun ASTNode.getTypeList(listType: ElementType, entryType: ElementType): List<ASTNode> =
    getParameterList(listType, entryType).map { it.getParameterType() }


fun ASTNode.getTypeArguments(): List<ASTNode> = getTypeList(ElementType.TypeArgumentList, ElementType.TypeProjection)

/* Constructs ParameterizedType representing ASTNode of type USER_TYPE/FUNCTION_TYPE/NULLABLE_TYPE */
fun ASTNode.toParameterizedType(binding: BindingContext): KotlinType {
    return binding.get(BindingContext.TYPE, this.psi.context as KtTypeReference) ?: error("Unrecognized element type: $elementType")
}
