package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import java.util.*

// TODO: rename
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
    val filtered = typeArgumentNode.filterChildren { n: ASTNode -> n.elementType.toString() == ElementType.REFERENCE_EXPRESSION.value }.toList()
    return filtered.mapNotNull { it.psi?.getFqName(binding) }
}

fun ASTNode.getFqNamesOfTypeArgument(binding: BindingContext) = getFqNamesOf(ElementType.CALL_EXPRESSION, ElementType.TYPE_ARGUMENT_LIST, binding)

fun ASTNode.getFqNamesOfValueArguments(binding: BindingContext) = getFqNamesOf(ElementType.CALL_EXPRESSION, ElementType.VALUE_ARGUMENT_LIST, binding)

/*
 * Traverse all children of the node (use BFS order) and return all children nodes which satisfy the filter condition
 */
fun ASTNode.filterChildren(filter: (node: ASTNode) -> Boolean): Sequence<ASTNode> {
    val filtered = ArrayList<ASTNode>()
    val nodes: Queue<ASTNode> = LinkedList<ASTNode>(listOf(this))
    nodes.addAll(this.children())
    while(nodes.isNotEmpty()) {
        val currentNode = nodes.poll()
        if (filter(currentNode)) {
            filtered.add(currentNode)
        }
        nodes.addAll(currentNode.children())
    }
    return filtered.asSequence()
}
