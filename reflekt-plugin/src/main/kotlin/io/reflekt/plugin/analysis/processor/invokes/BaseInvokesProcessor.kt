package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.*
import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseInvokesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    abstract val invokes: Output

    protected enum class ReflektFqNames(val fqName: String){
        OBJECTS("${Reflekt::class.qualifiedName!!}.objects"),
        CLASSES("${Reflekt::class.qualifiedName!!}.classes"),
        FUNCTIONS("${Reflekt::class.qualifiedName!!}.functions")
    }

    protected enum class ReflektFunctionsNames(val functionName: String) {
        WITH_SUBTYPE("withSubType"),
        WITH_SUBTYPES("${WITH_SUBTYPE.functionName}s"),
        WITH_ANNOTATIONS("withAnnotations")
    }

    protected fun findClassOrObjectInvokes(nodes: Sequence<ASTNode>): SubTypesToAnnotations {
        val subtypes = HashSet<String>()
        val annotations = HashSet<String>()

        for (node in nodes) {
            val callExpressionRoot = node.parents().firstOrNull { it.elementType.toString() == ElementType.CallExpression.value } ?: continue
            when(node.text) {
                ReflektFunctionsNames.WITH_SUBTYPE.functionName -> callExpressionRoot.getFqNamesOfTypeArgument(binding).let { subtypes.addAll(it) }
                ReflektFunctionsNames.WITH_SUBTYPES.functionName -> callExpressionRoot.getFqNamesOfValueArguments(binding).let { subtypes.addAll(it) }
                ReflektFunctionsNames.WITH_ANNOTATIONS.functionName -> {
                    callExpressionRoot.getFqNamesOfTypeArgument(binding).let { subtypes.addAll(it) }
                    callExpressionRoot.getFqNamesOfValueArguments(binding).let { annotations.addAll(it) }
                }
                else -> error("Found an unexpected node text: ${node.text}")
            }
        }
        return SubTypesToAnnotations(subtypes, annotations)
    }

    protected fun processClassOrObjectInvokes(element: KtElement): ClassOrObjectInvokes {
        val invokes: ClassOrObjectInvokes = HashSet()
        (element as? KtReferenceExpression)?.let {expression ->
            /*
             * Any Reflekt invoke is something like this: ... [1]Reflekt.[2]|objects()/classes() or so on|....
             * We can find the [2] place by fqName (it is KtReferenceExpression)
             * To find the [1] place we should go to the 1 level above from [2]
             * and find the root of the nested DOT_QUALIFIED_EXPRESSION nodes
             */
            val callExpressionRoot = expression.node.parents().first()
            callExpressionRoot.findLastParentByType(ElementType.DotQualifiedExpression)?.let { node ->
                /*
                 * Now we should find all current withSubTypes and withAnnotations invokes
                 */
                val filtered = node.filterChildren { n: ASTNode -> n.text in ReflektFunctionsNames.values().map { it.functionName } }
                invokes.add(findClassOrObjectInvokes(filtered))

            }
        }
        return invokes
    }

    protected abstract fun isValidExpression(expression: KtReferenceExpression): Boolean

    override fun shouldRunOn(element: KtElement) = (element as? KtReferenceExpression)?.let{ isValidExpression(it) } ?: false
}
