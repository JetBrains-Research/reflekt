package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.reflekt.util.Util.PLUGIN_ID

import org.jetbrains.kotlin.config.CompilerConfigurationKey

import java.io.File

/**
 * The Kotlin compiler configuration keys for the Reflekt plugin
 *
 * @property OUTPUT_DIR stores a relative path for generated files (e.g. ReflektImpl.kt)
 * @property ENABLED indicates if the plugin is enabled
 * @property DEPENDENCY_JARS stores the libraries jars absolute path that included in the current project
 *  as a compileClasspath configuration
 * @property REFLEKT_META_FILES stores the relative path to the ReflektMeta file
 *  in the resources directory in the src folder in the project
 * @property TO_SAVE_METADATA indicates whether to save Reflekt usages into META-INF
 * @property REFLEKT_META_PATH stores the relative path to the ReflektMeta file
 *  in the resources directory in the src folder in the project
 * @property LIBRARY_TO_INTROSPECT stores names of the libraries' for introspection
 *  (each library should be included in the project by a configuration that can be resolved in the compile-time)
 */
internal object Keys {
    val OUTPUT_DIR = CompilerConfigurationKey<File>("$PLUGIN_ID.outputDir")
    val ENABLED = CompilerConfigurationKey<Boolean>("$PLUGIN_ID.enabled")
    val DEPENDENCY_JARS = CompilerConfigurationKey<List<File>>("$PLUGIN_ID.dependencyJars")
    val REFLEKT_META_FILES = CompilerConfigurationKey<List<File>>("$PLUGIN_ID.reflektMetaFiles")
    val TO_SAVE_METADATA = CompilerConfigurationKey<Boolean>("$PLUGIN_ID.toSaveMetadata")
    val REFLEKT_META_PATH = CompilerConfigurationKey<String>("$PLUGIN_ID.reflektMeta")
    val LIBRARY_TO_INTROSPECT = CompilerConfigurationKey<List<String>>("$PLUGIN_ID.libraryToIntrospect")
}
