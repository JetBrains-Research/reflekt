package io.reflekt.plugin.utils

import io.reflekt.util.Util.PLUGIN_ID
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.io.File

internal object Keys {
    val OUTPUT_DIR = CompilerConfigurationKey<File>("$PLUGIN_ID.outputDir")
    val ENABLED = CompilerConfigurationKey<Boolean>("$PLUGIN_ID.enabled")
    val DEPENDENCY_JARS = CompilerConfigurationKey<List<File>>("$PLUGIN_ID.dependencyJars")
    val INTROSPECT_FILES = CompilerConfigurationKey<List<File>>("$PLUGIN_ID.introspectFiles")
}
