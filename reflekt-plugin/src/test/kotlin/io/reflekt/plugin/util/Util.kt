package io.reflekt.plugin.util

import com.beust.klaxon.Klaxon
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.reflect.KFunction

object Util {

    fun getResourcesRootPath(
        cls: KFunction<Any>,
        resourcesRootName: String = "data"
    ): String = cls.javaClass.getResource(resourcesRootName).path

    inline fun <reified T> parseJson(json: File): T {
        return Klaxon().parse<T>(json) ?: error("Failed to parse the file: ${json.absolutePath}")
    }

    /**
     * Represents a command passed to the [ProcessBuilder], where
     * [command] is a command to run (see [ProcessBuilder.command]),
     * [directory] is a working directory (see [ProcessBuilder.directory]),
     * and [environment] contains environment variables (see [ProcessBuilder.environment]).
     */
    data class Command(val command: List<String>, val directory: String? = null, val environment: Map<String, String>? = null)

    /*
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
}
