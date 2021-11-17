package org.jetbrains.reflekt.plugin

import org.jetbrains.reflekt.plugin.utils.Keys
import org.jetbrains.reflekt.util.Util.DEPENDENCY_JAR_OPTION_INFO
import org.jetbrains.reflekt.util.Util.ENABLED_OPTION_INFO
import org.jetbrains.reflekt.util.Util.LIBRARY_TO_INTROSPECT
import org.jetbrains.reflekt.util.Util.OUTPUT_DIR_OPTION_INFO
import org.jetbrains.reflekt.util.Util.PLUGIN_ID
import org.jetbrains.reflekt.util.Util.REFLEKT_META_FILE_OPTION_INFO
import org.jetbrains.reflekt.util.Util.REFLEKT_META_FILE_PATH
import org.jetbrains.reflekt.util.Util.SAVE_METADATA_OPTION_INFO

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

import java.io.File

@AutoService(CommandLineProcessor::class)
class ReflektCommandLineProcessor : CommandLineProcessor {
    /**
     * Just needs to be consistent with the key for ReflektSubPlugin#getCompilerPluginId
     */
    override val pluginId: String = PLUGIN_ID

    /**
     * Should match up with the options we return from our ReflektSubPlugin.
     * Should also have matching when branches for each name in the [processOption] function below
     */
    override val pluginOptions: Collection<CliOption> =
        listOf(
            ENABLED_OPTION,
            DEPENDENCY_JAR_OPTION,
            REFLEKT_META_FILE_OPTION,
            OUTPUT_DIR_OPTION,
            SAVE_METADATA_OPTION,
            REFLEKT_META_FILE_PATH_OPTION,
            LIBRARY_TO_INTROSPECT_OPTION,
        )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) = when (option) {
        ENABLED_OPTION -> configuration.put(Keys.ENABLED, value.toBoolean())
        DEPENDENCY_JAR_OPTION -> configuration.addToList(Keys.DEPENDENCY_JARS, File(value))
        REFLEKT_META_FILE_OPTION -> configuration.addToList(Keys.REFLEKT_META_FILES, File(value))
        OUTPUT_DIR_OPTION -> configuration.put(Keys.OUTPUT_DIR, File(value))
        SAVE_METADATA_OPTION -> configuration.put(Keys.TO_SAVE_METADATA, value.toBoolean())
        REFLEKT_META_FILE_PATH_OPTION -> configuration.put(Keys.REFLEKT_META_PATH, value)
        LIBRARY_TO_INTROSPECT_OPTION -> configuration.addToList(Keys.LIBRARY_TO_INTROSPECT, value)
        else -> error("Unexpected config option ${option.optionName}")
    }

    @Suppress("TYPE_ALIAS")
    private fun <T> CompilerConfiguration.addToList(configurationKey: CompilerConfigurationKey<List<T>>, value: T) {
        val values = get(configurationKey) ?: emptyList()
        put(configurationKey, values + value)
    }

    companion object {
        val ENABLED_OPTION =
            CliOption(
                optionName = ENABLED_OPTION_INFO.name,
                valueDescription = ENABLED_OPTION_INFO.valueDescription,
                description = ENABLED_OPTION_INFO.description,
            )
        val DEPENDENCY_JAR_OPTION =
            CliOption(
                optionName = DEPENDENCY_JAR_OPTION_INFO.name,
                valueDescription = DEPENDENCY_JAR_OPTION_INFO.valueDescription,
                description = DEPENDENCY_JAR_OPTION_INFO.description,
                allowMultipleOccurrences = true,
                required = false,
            )
        val REFLEKT_META_FILE_OPTION =
            CliOption(
                optionName = REFLEKT_META_FILE_OPTION_INFO.name,
                valueDescription = REFLEKT_META_FILE_OPTION_INFO.valueDescription,
                description = REFLEKT_META_FILE_OPTION_INFO.description,
                allowMultipleOccurrences = true,
                required = false,
            )
        val OUTPUT_DIR_OPTION =
            CliOption(
                optionName = OUTPUT_DIR_OPTION_INFO.name,
                valueDescription = OUTPUT_DIR_OPTION_INFO.valueDescription,
                description = OUTPUT_DIR_OPTION_INFO.description,
                required = false,
                allowMultipleOccurrences = false,
            )
        val SAVE_METADATA_OPTION =
            CliOption(
                optionName = SAVE_METADATA_OPTION_INFO.name,
                valueDescription = SAVE_METADATA_OPTION_INFO.valueDescription,
                description = SAVE_METADATA_OPTION_INFO.description,
                required = false,
                allowMultipleOccurrences = false,
            )
        val REFLEKT_META_FILE_PATH_OPTION =
            CliOption(
                optionName = REFLEKT_META_FILE_PATH.name,
                valueDescription = REFLEKT_META_FILE_PATH.valueDescription,
                description = REFLEKT_META_FILE_PATH.description,
                required = false,
                allowMultipleOccurrences = false,
            )
        val LIBRARY_TO_INTROSPECT_OPTION =
            CliOption(
                optionName = LIBRARY_TO_INTROSPECT.name,
                valueDescription = LIBRARY_TO_INTROSPECT.valueDescription,
                description = LIBRARY_TO_INTROSPECT.description,
                required = false,
                allowMultipleOccurrences = true,
            )
    }
}
