package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext

enum class ElementType(val value: String) {
    TYPE_ARGUMENT_LIST("TYPE_ARGUMENT_LIST"),
    VALUE_ARGUMENT_LIST("VALUE_ARGUMENT_LIST"),
    REFERENCE_EXPRESSION("REFERENCE_EXPRESSION"),
    LITERAL_STRING_TEMPLATE_ENTRY("LITERAL_STRING_TEMPLATE_ENTRY")
}

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    fun objects(vararg subtypes: String): Set<KtObjectDeclaration> {
        val objects = HashSet<KtObjectDeclaration>()
        val subtypesSet = subtypes.toSet()
        for (it in ktFiles) {
            it.visitObject {
                if (it.isSubtypeOf(subtypesSet, binding)) {
                    objects.add(it)
                }
            }
        }
        return objects
    }

    fun classes(vararg subtypes: String): Set<KtClass> {
        val classes = HashSet<KtClass>()
        val subtypesSet = subtypes.toSet()
        for (it in ktFiles) {
            it.visitClass {
                if (it.isSubtypeOf(subtypesSet, binding)) {
                    classes.add(it)
                }
            }
        }
        return classes
    }

    @ExperimentalStdlibApi
    fun invokes(): Pair<List<String>, List<String>> {
        val reflektObjectsName = "io.reflekt.Reflekt.Objects.withSubType"
        val reflektClassesName = "io.reflekt.Reflekt.Classes.withSubType"
        val fqNameObjects = mutableListOf<String>()
        val fqNameClasses = mutableListOf<String>()
        ktFiles.forEach{
            it.findReflektInvokes {
                when (it.getFqName(binding)) {
                    reflektObjectsName -> getFqName(it)?.let{ fqNameObjects.add(it) }
                    reflektClassesName -> getFqName(it)?.let{ fqNameClasses.add(it) }
                }
            }
        }
        return Pair(fqNameObjects, fqNameClasses)
    }

    @ExperimentalStdlibApi
    private fun getFqName(expression: KtReferenceExpression): String? {
        val rootExpressionChildren = expression.node.parents().first().children()
        // We are sure that these children are exist
        val typeArgumentNode = rootExpressionChildren.find { it.elementType.toString() == ElementType.TYPE_ARGUMENT_LIST.value }!!
        val valueArgumentNode = rootExpressionChildren.find { it.elementType.toString() == ElementType.VALUE_ARGUMENT_LIST.value }!!

        val typeFqName = typeArgumentNode.findNodeByNodeType(ElementType.REFERENCE_EXPRESSION)?.psi?.getFqName(binding)
        val valueFqName = valueArgumentNode.findNodeByNodeType(ElementType.LITERAL_STRING_TEMPLATE_ENTRY)?.text
        if (typeFqName == valueFqName) {
           return typeFqName
        }
        return null
    }

    @ExperimentalStdlibApi
    fun ASTNode.findNodeByNodeType(elementType: ElementType): ASTNode? {
        val nodes = mutableListOf(this)
        nodes.addAll(this.children())
        while (nodes.isNotEmpty()) {
            val currentNode = nodes.removeFirst()
            if (currentNode.elementType.toString() == elementType.value) {
                return currentNode
            }
            nodes.addAll(currentNode.children())
        }
        return null
    }
}
