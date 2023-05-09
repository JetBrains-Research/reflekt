package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.reflekt.plugin.utils.Util.initMessageCollector
import org.jetbrains.reflekt.plugin.utils.Util.log
import java.io.File

/**
 * Parses and stores the command line arguments from the plugin.
 *
 * @param configuration the current Kotlin compiler configuration or null
 * @param logFilePath path to the file with logs. By default, it is pathToKotlin/daemon/reflekt-log.log
 * @param isTestConfiguration indicates if the plugin is used in tests
 * @property enabled indicates if the plugin is enabled
 * @property reflektMetaFilesFromLibraries stores the absolute file's path from the library with Reflekt meta information
 * @property outputDir stores a relative path for generated files (e.g. ReflektImpl.kt)
 * @property toSaveMetadata indicates whether to save Reflekt usages into META-INF
 * @property reflektMetaFileRelativePath stores the relative path to the ReflektMeta file
 *  in the resources' directory of the projects' src folder
 * @property dependencyJars stores the absolute
 *  path of libraries jars included in the current project as a compileClasspath configuration
 * @property messageCollector [MessageCollector] for logs or null
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
class PluginConfig(
    configuration: CompilerConfiguration?,
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
            messageCollector = configuration[CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY]

            enabled = configuration[Keys.ENABLED] ?: isTestConfiguration
            outputDir = configuration[Keys.OUTPUT_DIR] ?: File(DEFAULT_GEN_FOLDER_PATH)

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

    /**
     * Builds the pretty string of the current configuration.
     *
     * @return [String]
     */
    private fun prettyString() = buildString {
        append("REFLEKT CONFIGURATION:\n")
        append("ENABLED: $enabled\n")
        append("REFLEKT META FILES FROM LIBRARIES: ${reflektMetaFilesFromLibraries.map { it.absolutePath }}\n")
        append("OUTPUT DIRECTORY: $outputDir\n")
        append("TO SAVE METADATA: $toSaveMetadata\n")
        append("REFLEKT METADATA RELATIVE PATH: $reflektMetaFileRelativePath\n")
        append("DEPENDENCY JARS: ${dependencyJars.map { it.absolutePath }}\n")
        append("_____________________________________________\n")
    }

    /**
     * Builds and logs pretty string of the current configuration.
     */
    private fun MessageCollector.logConfiguration() = this.log(prettyString())

    companion object {
        const val DEFAULT_GEN_FOLDER_PATH = "build/kotlin-gen"
    }
}
