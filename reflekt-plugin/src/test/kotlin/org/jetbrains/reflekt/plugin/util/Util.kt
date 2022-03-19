package org.jetbrains.reflekt.plugin.util

import java.io.*
import kotlin.reflect.KClass

object Util {

    fun getResourcesRootPath(
        cls: KClass<*>,
        resourcesRootName: String = "data",
    ): String = cls.java.getResource(resourcesRootName)?.path ?: error("Was not found the resource: $resourcesRootName")


    /**
     * Run ProcessBuilder and return output
     */
    fun runProcessBuilder(command: Command): String {
        val builder = ProcessBuilder(command.command)
        command.environment?.let {
            val environment = builder.environment()
            it.entries.forEach { e -> environment[e.key] = e.value }
        }
        command.directory?.let { builder.directory(File(it)) }
        builder.redirectErrorStream(true)
        val p = builder.start()
        return BufferedReader(InputStreamReader(p.inputStream)).readLines().joinToString(separator = "\n") { it }
    }

    fun File.clear() {
        require(this.isDirectory) { "${this.absolutePath} is not directory" }
        this.deleteRecursively()
        this.mkdir()
    }

    /**
     * Represents a command passed to the [ProcessBuilder], where
     * [command] is a command to run (see [ProcessBuilder.command]),
     * [directory] is a working directory (see [ProcessBuilder.directory]),
     * and [environment] contains environment variables (see [ProcessBuilder.environment]).
     * @property command
     * @property directory
     * @property environment
     */
    data class Command(
        val command: List<String>,
        val directory: String? = null,
        val environment: Map<String, String>? = null
    )
}
