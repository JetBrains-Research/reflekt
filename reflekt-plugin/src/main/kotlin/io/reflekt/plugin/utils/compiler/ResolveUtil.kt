package io.reflekt.plugin.utils.compiler

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.jvm.compiler.CliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.load.kotlin.PackagePartProvider
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace

object ResolveUtil {
    fun analyze(files: Collection<KtFile>, environment: KotlinCoreEnvironment): AnalysisResult = analyze(files, environment, environment.configuration)

    private fun analyze(
        files: Collection<KtFile>,
        environment: KotlinCoreEnvironment,
        configuration: CompilerConfiguration,
    ): AnalysisResult = analyze(environment.project, files, configuration) { environment.createPackagePartProvider(it) }

    private fun analyze(
        project: Project,
        files: Collection<KtFile>,
        configuration: CompilerConfiguration,
        trace: BindingTrace = CliBindingTrace(),
        factory: (GlobalSearchScope) -> PackagePartProvider,
    ): AnalysisResult = TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
        project, files, trace, configuration, factory,
    )
}
