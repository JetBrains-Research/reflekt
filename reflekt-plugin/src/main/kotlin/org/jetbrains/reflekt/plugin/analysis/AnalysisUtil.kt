package org.jetbrains.reflekt.plugin.analysis

import java.io.File
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.reflekt.plugin.analysis.analyzer.source.BaseAnalyzer
import org.jetbrains.reflekt.plugin.analysis.analyzer.source.ReflektAnalyzer
import org.jetbrains.reflekt.plugin.utils.compiler.*

@Suppress("AVOID_USING_UTILITY_CLASS")
object AnalysisUtil {
    fun getReflektAnalyzer(classPath: Set<File>, sources: Set<File>, messageCollector: MessageCollector? = null): ReflektAnalyzer {
        val baseAnalyzer = getBaseAnalyzer(classPath, sources, messageCollector)
        return ReflektAnalyzer(baseAnalyzer.ktFiles, baseAnalyzer.binding, baseAnalyzer.messageCollector)
    }

    fun getBaseAnalyzer(classPath: Set<File>, sources: Set<File>, messageCollector: MessageCollector? = null): BaseAnalyzer {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = parseKtFiles(sources, environment)
        val resolved = analyze(ktFiles, environment)
        return BaseAnalyzer(ktFiles, resolved.bindingContext, messageCollector)
    }
}
