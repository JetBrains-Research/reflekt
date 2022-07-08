package org.jetbrains.reflekt.plugin.compiler.providers.reflekt

import com.intellij.mock.MockProject
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.reflekt.plugin.ReflektComponentRegistrar

class ReflektPluginWithStandaloneProjectProvider(testServices: TestServices) : ReflektPluginProviderBase(testServices) {
    override fun registerCompilerExtensions(project: Project, module: TestModule, configuration: CompilerConfiguration) {
        ReflektComponentRegistrar(true).registerProjectComponents(project as MockProject, configuration)
    }
}

class ReflektPluginWithlLibraryProvider(testServices: TestServices) : ReflektPluginProviderBase(testServices) {
    override fun registerCompilerExtensions(project: Project, module: TestModule, configuration: CompilerConfiguration) {
        TODO("Create a library test config")
    }
}
