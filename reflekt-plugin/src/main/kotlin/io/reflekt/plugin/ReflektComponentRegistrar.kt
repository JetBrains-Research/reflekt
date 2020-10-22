package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.plugin.util.FileUtil
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }
        val filesToIntrospect = getFilesToIntrospect(configuration[KEY_JAR_FILES])

//        project.baseDir

        // TODO: registerExtension
    }

    private fun getFilesToIntrospect(jars: List<String>?): Set<File> {
        val filesToIntrospect: MutableSet<File> = HashSet()
        jars?.forEach {
            val file = File(it)
            if (file.isFile) {
                filesToIntrospect.addAll(FileUtil.extractAllFiles(file))
            }
        }
        return filesToIntrospect
    }

    private fun getProjectFiles() {
        TODO("Not implemented yet")
    }
}

