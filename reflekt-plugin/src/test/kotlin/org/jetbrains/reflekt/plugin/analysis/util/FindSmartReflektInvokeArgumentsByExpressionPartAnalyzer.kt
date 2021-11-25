package org.jetbrains.reflekt.plugin.analysis.util

import org.jetbrains.reflekt.plugin.analysis.analyzer.source.BaseAnalyzer
import org.jetbrains.reflekt.plugin.analysis.common.findSmartReflektInvokeArgumentsByExpressionPart
import org.jetbrains.reflekt.plugin.analysis.models.SupertypesToFilters
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.psi.visit
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class SmartReflektTestAnalyzer(baseAnalyzer: BaseAnalyzer) : BaseAnalyzer(baseAnalyzer.ktFiles, baseAnalyzer.binding) {
    private fun fileToExpressions(): HashMap<FileId, MutableList<KtNameReferenceExpression>> {
        val processor = SmartReflektExpressionProcessor(binding)
        ktFiles.forEach { file ->
            file.visit(setOf(processor))
        }
        return processor.fileToExpressions
    }

    fun analyze(): Set<SupertypesToFilters> {
        val result = HashSet<SupertypesToFilters>()
        fileToExpressions().forEach { (_, expressions) ->
            expressions.forEach { e ->
                findSmartReflektInvokeArgumentsByExpressionPart(e, binding)?.let {
                    result.add(it)
                }
            }
        }
        return result
    }
}
