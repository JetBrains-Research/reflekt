package io.reflekt.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@AutoService(CommandLineProcessor::class)
class ReflektCommandLineProcessor : CommandLineProcessor {
  /**
   * Just needs to be consistent with the key for ReflektSubPlugin#getCompilerPluginId
   */
  override val pluginId: String = "io.reflekt"

  /**
   * Should match up with the options we return from our ReflektSubPlugin.
   * Should also have matching when branches for each name in the [processOption] function below
   */
  override val pluginOptions: Collection<CliOption> = listOf(
      CliOption(
          optionName = "enabled", valueDescription = "<true|false>",
          description = "whether to enable the debuglog plugin or not"
      ),
      CliOption(
          optionName = "librariesToIntrospect", valueDescription = "<libraryName>",
          description = "Libraries to introspect, which are in the project dependencies in the format: \"\$group:\$name:\$version\"",
          required = true, allowMultipleOccurrences = true
      )
  )
}

val KEY_ENABLED = CompilerConfigurationKey<Boolean>("whether the plugin is enabled")
val KEY_LIBRARIES = CompilerConfigurationKey<List<String>>("libraries to introspect")
