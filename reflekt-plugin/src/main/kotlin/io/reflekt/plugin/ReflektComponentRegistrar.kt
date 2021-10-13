package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.plugin.analysis.ReflektModuleAnalysisExtension
import io.reflekt.plugin.analysis.models.ReflektContext
import io.reflekt.plugin.generation.ir.ReflektIrGenerationExtension
import io.reflekt.plugin.generation.ir.SmartReflektIrGenerationExtension
import io.reflekt.plugin.utils.Keys
import io.reflekt.plugin.utils.Util.initMessageCollector
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.Util.messageCollector
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

@AutoService(ComponentRegistrar::class)
class ReflektComponentRegistrar(private val hasConfiguration: Boolean = true) : ComponentRegistrar {
    // The path will be: pathToKotlin/daemon/reflekt-log.log
    private val logFilePath = "reflekt-log.log"

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        // TODO: create a class for Reflekt configs
        if (hasConfiguration && configuration[Keys.ENABLED] != true) {
            return
        }
        configuration.initMessageCollector(logFilePath)
        if (configuration[Keys.REFLEKT_META_FILES] != null && configuration[Keys.OUTPUT_DIR] == null) {
            error("Output path not specified")
        }

        val toSaveMetadata = configuration[Keys.TO_SAVE_METADATA] ?: false
        configuration.messageCollector.log("TO SAVE METADATA FLAG $toSaveMetadata;")
        val reflektMetaFilePath = configuration[Keys.REFLEKT_META_PATH]
        if (toSaveMetadata && reflektMetaFilePath == null) {
            error("Resources folder was not set or doe not exist, but toSaveMetadata is $toSaveMetadata")
        }

        val dependencyJars = configuration[Keys.DEPENDENCY_JARS] ?: emptyList()
        configuration.messageCollector.log("DEPENDENCY JARS: ${dependencyJars.map { it.absolutePath }};")

        configuration.messageCollector.log("INTROSPECT CLASS FILES: ${configuration[Keys.REFLEKT_META_FILES]};")
        val reflektMetaFiles = configuration[Keys.REFLEKT_META_FILES]?.toSet() ?: emptySet()
        val reflektContext = ReflektContext()

        configuration.messageCollector.log("PROJECT FILE PATH: ${project.projectFilePath}")

        // This will be called multiple times (for each project module),
        // since compilation process runs module by module
        AnalysisHandlerExtension.registerExtension(
            project,
            ReflektModuleAnalysisExtension(
                reflektMetaFiles = reflektMetaFiles,
                toSaveMetadata = toSaveMetadata,
                generationPath = configuration[Keys.OUTPUT_DIR],
                reflektContext = reflektContext,
                messageCollector = configuration.messageCollector,
                reflektMetaFile = reflektMetaFilePath?.let { File(it) }
            )
        )
        IrGenerationExtension.registerExtension(
            project,
            ReflektIrGenerationExtension(
                reflektContext = reflektContext,
                messageCollector = configuration.messageCollector,
                toReplaceIr = toSaveMetadata
            )
        )
        IrGenerationExtension.registerExtension(
            project,
            SmartReflektIrGenerationExtension(
                classpath = dependencyJars,
                reflektContext = reflektContext,
                messageCollector = configuration.messageCollector,
                toReplaceIr = toSaveMetadata
            )
        )
    }
}

