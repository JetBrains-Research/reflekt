package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import java.util.*
import kotlin.collections.HashSet
import kotlin.reflect.KFunction3

enum class ElementType(val value: String) {
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
}

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    fun objects(vararg subtypes: String) = classesOrObjects(subtypes.toSet(), KtFile::visitObject)

    fun classes(vararg subtypes: String) = classesOrObjects(subtypes.toSet(), KtFile::visitClass)

    private fun classesOrObjects(subtypes: Set<String>,
                                 visitor: KFunction3<KtFile, (KtClassOrObject) -> Boolean, (KtClassOrObject) -> Unit, Unit>): Set<KtClassOrObject> {
        val classesOrObjects = HashSet<KtClassOrObject>()
        ktFiles.forEach { file ->
            visitor(file, { it.isSubtypeOf(subtypes, binding)}, { classesOrObjects.add(it) })
        }
        return classesOrObjects
    }

    fun invokes(reflektObjectsName: String, reflektClassesName: String): Pair<List<String>, List<String>> {
        val fqNameObjects = mutableListOf<String>()
        val fqNameClasses = mutableListOf<String>()
        ktFiles.forEach { file ->
            file.visitReferenceExpression {
                when (it.getFqName(binding)) {
                    reflektObjectsName -> getFqName(it)?.let { fqNameObjects.add(it) }
                    reflektClassesName -> getFqName(it)?.let { fqNameClasses.add(it) }
                }
            }
        }
        return Pair(fqNameObjects, fqNameClasses)
    }

    private fun getFqName(expression: KtReferenceExpression): String? {
        val rootExpressionChildren = expression.node.parents().first().children()
        // We are sure that these children are exist
        val typeArgumentNode = rootExpressionChildren.find { it.elementType.toString() == ElementType.TYPE_ARGUMENT_LIST.value }!!
        return typeArgumentNode.findNodeByNodeType(ElementType.REFERENCE_EXPRESSION)?.psi?.getFqName(binding)
    }

    private fun ASTNode.findNodeByNodeType(elementType: ElementType): ASTNode? {
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
}
