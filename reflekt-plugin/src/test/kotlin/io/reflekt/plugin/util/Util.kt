package io.reflekt.plugin.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.internal.impldep.org.apache.commons.lang.SystemUtils
import java.io.*
import kotlin.reflect.KClass


object Util {
    val gson: Gson = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()

    fun getResourcesRootPath(
        cls: KClass<*>,
        resourcesRootName: String = "data"
    ): String = cls.java.getResource(resourcesRootName)?.path ?: error("Was not found the resource: ${resourcesRootName}")

    inline fun <reified T> parseJson(json: File): T =
        gson.fromJson(json.readText(), T::class.java)

    inline fun <reified T> toJson(value: T): String =
        gson.toJson(value)

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

    fun getTempPath(): String {
        val file = File(System.getProperty("java.io.tmpdir"))
        try {
            val canonical = file.canonicalPath
            if (!SystemUtils.IS_OS_WINDOWS || !canonical.contains(" ")) {
                return canonical
            }
        } catch (ignore: IOException) {
            // This error is ok
        }
        return file.absolutePath
    }

     fun File.clear() {
        require(this.isDirectory) { "${this.absolutePath} is not directory" }
        this.deleteRecursively()
        this.mkdir()
    }
}
