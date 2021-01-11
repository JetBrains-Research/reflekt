package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.analyzer.*
import io.reflekt.plugin.utils.compiler.EnvironmentManager
import io.reflekt.plugin.utils.compiler.ParseUtil
import io.reflekt.plugin.utils.compiler.ResolveUtil
import java.io.File

object AnalysisUtil {

    fun getReflektAnalyzer(classPath: Set<File>, sources: Set<File>): ReflektAnalyzer {
        val baseAnalyzer = getBaseAnalyzer(classPath, sources)
        return  ReflektAnalyzer(baseAnalyzer.ktFiles, baseAnalyzer.binding)
    }

    fun getSmartReflektAnalyzer(classPath: Set<File>, sources: Set<File>): SmartReflektAnalyzer {
        val baseAnalyzer = getBaseAnalyzer(classPath, sources)
        return SmartReflektAnalyzer(baseAnalyzer.ktFiles, baseAnalyzer.binding)
    }

    fun getBaseAnalyzer(classPath: Set<File>, sources: Set<File>): BaseAnalyzer {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = ParseUtil.analyze(sources, environment)
        val resolved = ResolveUtil.analyze(ktFiles, environment)
        return BaseAnalyzer(ktFiles, resolved.bindingContext)
    }
}
