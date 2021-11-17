package io.reflekt.util

object Util {
    /** Global constant with plugin identifier */
    const val PLUGIN_ID = "io.reflekt"
    const val GRADLE_GROUP_ID = "io.reflekt"

    /**
     * Just needs to be consistent with the artifactId in reflekt-plugin build.gradle.kts#publishJar
     */
    const val GRADLE_ARTIFACT_ID = "reflekt-plugin"
    const val VERSION = "1.5.31"
    val ENABLED_OPTION_INFO = MyCliOption(
        name = "enabled",
        valueDescription = "<true|false>",
        description = "Whether to enable the Reflekt plugin or not",
    )
    val DEPENDENCY_JAR_OPTION_INFO =
        MyCliOption(
            name = "dependencyJar",
            valueDescription = "<dependency jar>",
            description = "Project dependency jar file",
        )
    val REFLEKT_META_FILE_OPTION_INFO =
        MyCliOption(
            name = "reflektMetaFile",
            valueDescription = "<file's path>",
            description = "File's path from the library with Reflekt meta infromation",
        )
    val OUTPUT_DIR_OPTION_INFO =
        MyCliOption(
            name = "outputDir",
            valueDescription = "<path>",
            description = "Resulting generated files",
        )
    val SAVE_METADATA_OPTION_INFO = MyCliOption(
        name = "toSaveMetadata",
        valueDescription = "<true|false>",
        description = "Whether to save Reflekt usages into META-INF",
    )
    val LIBRARY_TO_INTROSPECT = MyCliOption(
        name = "introspectedLibrary",
        valueDescription = "<library name>",
        description = "Names of the libraries' for introspection",
    )
    val REFLEKT_META_FILE_PATH = MyCliOption(
        name = "reflektMetaPath",
        valueDescription = "<path to the ReflektMeta file>",
        description = "Path to the ReflektMeta file in the resources dir in the src folder",
    )

    /**
     * @property name
     * @property valueDescription
     * @property description
     */
    data class MyCliOption(
        val name: String,
        val valueDescription: String,
        val description: String,
    )
}
