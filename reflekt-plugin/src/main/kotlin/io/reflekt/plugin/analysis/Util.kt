package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.analyzer.source.*
import io.reflekt.plugin.utils.compiler.*
import java.io.File

@Suppress("AVOID_USING_UTILITY_CLASS")
object AnalysisUtil {
    fun getReflektAnalyzer(classPath: Set<File>, sources: Set<File>): ReflektAnalyzer {
        val baseAnalyzer = getBaseAnalyzer(classPath, sources)
        return ReflektAnalyzer(baseAnalyzer.ktFiles, baseAnalyzer.binding)
    }

    fun getBaseAnalyzer(classPath: Set<File>, sources: Set<File>): BaseAnalyzer {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = ParseUtil.analyze(sources, environment)
        val resolved = ResolveUtil.analyze(ktFiles, environment)
        return BaseAnalyzer(ktFiles, resolved.bindingContext)
    }
}
