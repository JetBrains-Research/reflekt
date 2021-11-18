package io.reflekt.plugin.utils

import io.reflekt.plugin.utils.Util.initMessageCollector
import io.reflekt.plugin.utils.Util.log
import io.reflekt.plugin.utils.Util.messageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.io.File

class PluginConfig(
    configuration: CompilerConfiguration?,
    // The path will be: pathToKotlin/daemon/reflekt-log.log
    logFilePath: String = "reflekt-log.log",
    isTestConfiguration: Boolean = false,
) {
    var enabled = isTestConfiguration
    var reflektMetaFilesFromLibraries: Set<File> = emptySet()
    var outputDir: File? = null
    var toSaveMetadata = false
    var reflektMetaFileRelativePath: String? = null
    var dependencyJars: List<File> = emptyList()
    // TODO: make [messageCollector] available from any part of code
    var messageCollector: MessageCollector? = null

    init {
        configuration?.let {
            configuration.initMessageCollector(logFilePath)
            messageCollector = configuration.messageCollector

            enabled = configuration[Keys.ENABLED] ?: false
            outputDir = configuration[Keys.OUTPUT_DIR] ?: File("src/main/kotlin-gen")

            toSaveMetadata = configuration[Keys.TO_SAVE_METADATA] ?: false
            reflektMetaFileRelativePath = configuration[Keys.REFLEKT_META_PATH]
            if (toSaveMetadata && reflektMetaFileRelativePath == null) {
                error("The relative path ro the ReflektMeta file was not set, but toSaveMetadata flag is $toSaveMetadata")
            }
            dependencyJars = configuration[Keys.DEPENDENCY_JARS] ?: emptyList()
            reflektMetaFilesFromLibraries = configuration[Keys.REFLEKT_META_FILES]?.toSet() ?: emptySet()

            messageCollector?.logConfiguration()
        }
    }

    private fun MessageCollector.logConfiguration() {
        val log = buildString {
            append("REFLEKT CONFIGURATION:\n")
            append("ENABLED: $enabled\n")
            append("REFLEKT META FILES FROM LIBRARIES: ${reflektMetaFilesFromLibraries.map { it.absolutePath }}\n")
            append("OUTPUT DIRECTORY: $outputDir\n")
            append("TO SAVE METADATA: $toSaveMetadata\n")
            append("REFLEKT METADATA RELATIVE PATH: $reflektMetaFileRelativePath\n")
            append("DEPENDENCY JARS: ${dependencyJars.map { it.absolutePath }}\n")
            append("_____________________________________________\n")
        }
        this.log(log)
    }
}
