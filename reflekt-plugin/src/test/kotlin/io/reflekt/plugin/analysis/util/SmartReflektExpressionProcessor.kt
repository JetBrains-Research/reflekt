package io.reflekt.plugin.analysis.util

import io.reflekt.SmartReflekt
import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.processor.*
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.utils.enumToRegexOptions
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class SmartReflektExpressionProcessor(override val binding: BindingContext) : Processor<MutableList<KtNameReferenceExpression>>(binding) {
    val fileToExpressions: HashMap<FileID, MutableList<KtNameReferenceExpression>> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, MutableList<KtNameReferenceExpression>> {
        (element as? KtNameReferenceExpression)?.let {
            fileToExpressions.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToExpressions
    }

    private fun isValidExpression(expression: KtNameReferenceExpression): Boolean {
        val names = enumToRegexOptions(ReflektEntity.values(), ReflektEntity::entityType)
        val fqName = expression.getFqName(binding) ?: return false
        Regex("${SmartReflekt::class.qualifiedName}\\.$names").matchEntire(fqName) ?: return false
        return true
    }

    override fun shouldRunOn(element: KtElement): Boolean = (element as? KtNameReferenceExpression)?.let { isValidExpression(it) } ?: false
}
