package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.ElementType
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
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
    val index = parents.indexOfFirst { it.elementType.toString() != elementType.value }
    if (index <= 0) {
        return null
    }
    return parents[index - 1]
}

fun ASTNode.getFqNamesOf(rootType: ElementType, type: ElementType, binding: BindingContext): List<String> {
    require(this.elementType.toString() == rootType.value) { "Invalid element type ${this.elementType} of the parent node ${this.text}" }
    val typeArgumentNode = this.children().find { it.elementType.toString() == type.value }!!
    val filtered = typeArgumentNode.filterChildren { n: ASTNode -> n.elementType.toString() == ElementType.ReferenceExpression.value }.toList()
    return filtered.mapNotNull { it.psi?.getFqName(binding) }
}

fun ASTNode.getFqNamesOfTypeArgument(binding: BindingContext) = getFqNamesOf(ElementType.CallExpression, ElementType.TypeArgumentList, binding)

fun ASTNode.getFqNamesOfValueArguments(binding: BindingContext) = getFqNamesOf(ElementType.CallExpression, ElementType.ValueArgumentList, binding)

fun ASTNode.getLambdaBody(): String {
    require(this.elementType.toString() == ElementType.CallExpression.value) { "Try to get lambda body from the node with type: ${this.elementType}" }
    // CALL_EXPRESSION -> LAMBDA_ARGUMENT -> LAMBDA_EXPRESSION -> FUNCTION_LITERAL -> BLOCK
    // We need to get the text from the last level (BLOCK)
    return this.children().firstOrNull { it.elementType.toString() == ElementType.LambdaArgument.value }
        .firstOrNull { it.elementType.toString() == ElementType.LambdaExpression.value }
        .firstOrNull { it.elementType.toString() == ElementType.FunctionLiteral.value }
        .firstOrNull { it.elementType.toString() == ElementType.Block.value }
        .text ?: error("Incorrect lambda structure in the CALL_EXPRESSION node")
}

/*
 * Traverse all children of the node (use BFS order) and return all children nodes which satisfy the filter condition
 */
fun ASTNode.filterChildren(filter: (node: ASTNode) -> Boolean): Sequence<ASTNode> {
    val filtered = ArrayList<ASTNode>()
    val nodes: Queue<ASTNode> = LinkedList<ASTNode>(listOf(this))
    nodes.addAll(this.children())
    while (nodes.isNotEmpty()) {
        val currentNode = nodes.poll()
        if (filter(currentNode)) {
            filtered.add(currentNode)
        }
        nodes.addAll(currentNode.children())
    }
    return filtered.asSequence()
}
