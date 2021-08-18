package io.reflekt.plugin.analysis.util

import io.reflekt.plugin.analysis.analyzer.BaseAnalyzer
import io.reflekt.plugin.analysis.common.findSmartReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.models.SupertypesToFilters
import io.reflekt.plugin.analysis.psi.visit
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class SmartReflektTestAnalyzer(baseAnalyzer: BaseAnalyzer) : BaseAnalyzer(baseAnalyzer.ktFiles, baseAnalyzer.binding) {
    private fun expressions(): MutableList<KtNameReferenceExpression> {
        val processor = SmartReflektExpressionProcessor(binding)
        ktFiles.forEach { file ->
            file.visit(setOf(processor))
        }
        return processor.expressions
    }

    fun analyze(): Set<SupertypesToFilters> {
        val result = HashSet<SupertypesToFilters>()
        expressions().forEach { expression ->
            findSmartReflektInvokeArgumentsByExpressionPart(expression, binding)?.let {
                result.add(it)
            }
        }
        return result
    }
}
