package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.psi.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.collections.HashSet

class ReflektAnalyzer(private val ktFiles: Set<KtFile>, private val binding: BindingContext) {
    fun objects(filter: (KtClassOrObject, BindingContext) -> Boolean)
        = classesOrObjects(KtFile::visitObject, filter)

    fun classes(filter: (KtClassOrObject, BindingContext) -> Boolean)
        = classesOrObjects(KtFile::visitClass, filter)

    private fun classesOrObjects(visitor: (KtFile, (KtClassOrObject) -> Boolean, (KtClassOrObject) -> Unit) -> Unit,
                                 filter: (KtClassOrObject, BindingContext) -> Boolean): Set<KtClassOrObject> {
        val classesOrObjects = HashSet<KtClassOrObject>()
        ktFiles.forEach { file ->
            visitor(file, { filter(it, binding) }, { classesOrObjects.add(it) })
        }
        return classesOrObjects
    }

    fun invokes(reflektNames: FunctionsFqNames): Invokes {
        val invokes = Invokes()
        ktFiles.forEach { file ->
            file.visitReferenceExpression { expression ->
                val fqName = expression.getFqName(binding)
                if (fqName in reflektNames.names) {
                    val callExpressionRoot = expression.node.parents().first()
                    callExpressionRoot.getFqNameOfTypeArgument(binding)?.let {
                        when (fqName) {
                            reflektNames.withSubTypeObjects -> invokes.withSubTypeObjects.add(it)
                            reflektNames.withSubTypeClasses -> invokes.withSubTypeClasses.add(it)
                            reflektNames.withAnnotationObjects -> {
                                callExpressionRoot.withSubTypeRoot().getFqNameOfTypeArgument(binding)?.let { withSubTypeFqName ->
                                    invokes.withAnnotationObjects.getOrPut(withSubTypeFqName, { mutableListOf() }).add(it)
                                }
                            }
                            reflektNames.withAnnotationClasses -> {
                                callExpressionRoot.withSubTypeRoot().getFqNameOfTypeArgument(binding)?.let { withSubTypeFqName ->
                                    invokes.withAnnotationClasses.getOrPut(withSubTypeFqName, { mutableListOf() }).add(it)
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        }
        return invokes
    }
}
