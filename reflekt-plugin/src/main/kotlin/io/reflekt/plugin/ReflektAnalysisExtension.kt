package io.reflekt.plugin

import io.reflekt.plugin.analysis.ReflektAnalyzer
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

class ReflektAnalysisExtension(private val filesToIntrospect: Set<KtFile>) : AnalysisHandlerExtension {

    override fun doAnalysis(project: Project,
                            module: ModuleDescriptor,
                            projectContext: ProjectContext,
                            files: Collection<KtFile>,
                            bindingTrace: BindingTrace,
                            componentProvider: ComponentProvider): AnalysisResult? {
        val analyzer = ReflektAnalyzer(files.toSet().union(filesToIntrospect), bindingTrace.bindingContext)
        val invokes = analyzer.invokes()
        val uses = analyzer.uses(invokes)
        println("USES: ${uses}")
        // TODO: add uses into bindingContext???

        return super.doAnalysis(project, module, projectContext, files, bindingTrace, componentProvider)
    }

}
