package io.reflekt.plugin

import com.google.auto.service.AutoService
import io.reflekt.cli.Util.ENABLED_OPTION_INFO
import io.reflekt.cli.Util.INTROSPECT_FILE_OPTION_INFO
import io.reflekt.cli.Util.OUTPUT_DIR_OPTION_INFO
import io.reflekt.cli.Util.PLUGIN_ID
import io.reflekt.plugin.utils.Keys
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
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
    override val pluginOptions: Collection<CliOption> = listOf(ENABLED_OPTION, INTROSPECT_FILE_OPTION, OUTPUT_DIR_OPTION)

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        return when (option) {
            ENABLED_OPTION -> configuration.put(Keys.ENABLED, value.toBoolean())
            INTROSPECT_FILE_OPTION -> {
                // Todo: can we do it better?
                val files = configuration.get(Keys.INTROSPECT_FILES) ?: emptyList()
                configuration.put(Keys.INTROSPECT_FILES, files + File(value))
            }
            OUTPUT_DIR_OPTION -> configuration.put(Keys.OUTPUT_DIR, File(value))
            else -> error("Unexpected config option ${option.optionName}")
        }
    }

    companion object {
        val ENABLED_OPTION =
            CliOption(
                optionName = ENABLED_OPTION_INFO.name,
                valueDescription = ENABLED_OPTION_INFO.valueDescription,
                description = ENABLED_OPTION_INFO.description
            )

        val INTROSPECT_FILE_OPTION =
            CliOption(
                optionName = INTROSPECT_FILE_OPTION_INFO.name,
                valueDescription = INTROSPECT_FILE_OPTION_INFO.valueDescription,
                description = INTROSPECT_FILE_OPTION_INFO.description,
                allowMultipleOccurrences = true,
                required = false
            )

        val OUTPUT_DIR_OPTION =
            CliOption(
                optionName = OUTPUT_DIR_OPTION_INFO.name,
                valueDescription = OUTPUT_DIR_OPTION_INFO.valueDescription,
                description = OUTPUT_DIR_OPTION_INFO.description,
                required = false,
                allowMultipleOccurrences = false
            )
    }
}
