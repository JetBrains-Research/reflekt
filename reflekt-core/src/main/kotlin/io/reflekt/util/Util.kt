package io.reflekt.cli

data class MyCliOption(
    val name: String,
    val valueDescription: String,
    val description: String
)


object Util {
    /** Global constant with plugin identifier */
    const val PLUGIN_ID = "io.reflekt"

    const val GRADLE_GROUP_ID = "io.reflekt.gradle"
    /**
     * Just needs to be consistent with the artifactId in reflekt-plugin build.gradle.kts#publishJar
     */
    const val GRADLE_ARTIFACT_ID = "reflekt-compiler-plugin"
    const val VERSION = "0.1.0"

    val ENABLED_OPTION_INFO = MyCliOption(
        name = "enabled",
        valueDescription = "<true|false>",
        description = "whether to enable the Reflekt plugin or not"
    )

    val INTROSPECT_FILE_OPTION_INFO =
        MyCliOption(
            name = "fileToIntrospect",
            valueDescription = "<file's path>",
            description = "File's path from the library to introspect"
        )

    val OUTPUT_DIR_OPTION_INFO =
        MyCliOption(
            name = "outputDir",
            valueDescription = "<path>",
            description = "Resulting generated files"
        )
}
