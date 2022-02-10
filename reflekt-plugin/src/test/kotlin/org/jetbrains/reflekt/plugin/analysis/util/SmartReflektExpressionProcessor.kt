package org.jetbrains.reflekt.plugin.analysis.util

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.processor.fullName
import org.jetbrains.reflekt.plugin.analysis.processor.source.Processor
import org.jetbrains.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.reflekt.plugin.utils.enumToRegexOptions

class SmartReflektExpressionProcessor(override val binding: BindingContext) : Processor<MutableList<KtNameReferenceExpression>, KtElement, KtFile>(binding) {
    val fileToExpressions: HashMap<FileId, MutableList<KtNameReferenceExpression>> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileId, MutableList<KtNameReferenceExpression>> {
        (element as? KtNameReferenceExpression)?.let {
            fileToExpressions.getOrPut(file.fullName) { ArrayList() }.add(it)
        }
        return fileToExpressions
    }

    @Suppress("ReturnCount")
    private fun isValidExpression(expression: KtNameReferenceExpression): Boolean {
        val names = enumToRegexOptions(ReflektEntity.values(), ReflektEntity::entityType)
        val fqName = expression.getFqName(binding) ?: return false
        Regex("${SmartReflekt::class.qualifiedName}\\.$names").matchEntire(fqName) ?: return false
        return true
    }

    override fun shouldRunOn(element: KtElement): Boolean = (element as? KtNameReferenceExpression)?.let { isValidExpression(it) } ?: false
}
