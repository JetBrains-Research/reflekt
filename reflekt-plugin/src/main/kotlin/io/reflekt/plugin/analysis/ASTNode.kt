package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import java.util.*

fun ASTNode.getFqNameOfTypeArgument(binding: BindingContext): String? {
    require(this.elementType.toString() == ElementType.CALL_EXPRESSION.value) { "Invalid element type ${this.elementType} of the parent node" }
    // We are sure that these children are exist
    val typeArgumentNode = this.children().find { it.elementType.toString() == ElementType.TYPE_ARGUMENT_LIST.value }!!
    return typeArgumentNode.findNodeByNodeType(ElementType.REFERENCE_EXPRESSION)?.psi?.getFqName(binding)
}

fun ASTNode.withSubTypeRoot(): ASTNode {
    // Todo-birillo: find a better way for it - after changing Reflekt architecture,
    //  because probably we should find withSubType node by another way
    return this.parents().first().children().first().children().last()
}

//TODO-birillo you may try to use findChildByType
fun ASTNode.findNodeByNodeType(elementType: ElementType): ASTNode? {
    val nodes: Queue<ASTNode> = LinkedList<ASTNode>(listOf(this))
    nodes.addAll(this.children())
    while (nodes.isNotEmpty()) {
        val currentNode = nodes.poll()
        if (currentNode.elementType.toString() == elementType.value) {
            return currentNode
        }
        nodes.addAll(currentNode.children())
    }
    return null
}
