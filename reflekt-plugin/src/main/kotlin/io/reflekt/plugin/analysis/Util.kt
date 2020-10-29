package io.reflekt.plugin.analysis

import io.reflekt.plugin.utils.compiler.EnvironmentManager
import io.reflekt.plugin.utils.compiler.ParseUtil
import io.reflekt.plugin.utils.compiler.ResolveUtil
import java.io.File

object AnalysisUtil {

    fun getReflektAnalyzer(classPath: Set<File> = emptySet(), sources: Set<File>): ReflektAnalyzer {
        val environment = EnvironmentManager.create(classPath)
        val ktFiles = ParseUtil.analyze(sources, environment)
        val resolved = ResolveUtil.analyze(ktFiles, environment)
        return  ReflektAnalyzer(ktFiles, resolved.bindingContext)
    }

}
