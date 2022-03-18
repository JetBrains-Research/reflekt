package org.jetbrains.reflekt.plugin.analysis

import java.io.File
import kotlin.reflect.KClass
import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.jetbrains.reflekt.util.file.getNestedDirectories

fun File.findInDirectory(name: String, createIfDoesNotExist: Boolean = false): File {
    if (!this.isDirectory) {
        error("${this.absolutePath} is not a directory")
    }
    val baseErrorMessage = "in the directory ${this.name} was not found"
    return this.listFiles()?.firstOrNull { it.name == name } ?: run {
        if (createIfDoesNotExist) {
            val res = File("${this.absolutePath}/$name")
            res.mkdirs()
            res
        } else {
            error("$name $baseErrorMessage")
        }
    }
}

fun getProjectFilesInDirectory(directory: File): Set<File> =
    directory.findInDirectory("project", true).absolutePath.getAllNestedFiles(ignoredDirectories = setOf(".idea"))
        .toSet()

fun getTestsDirectories(cls: KClass<*>): List<File> = Util.getResourcesRootPath(cls).getNestedDirectories().sorted()
    .filter { "test" in it.name }

/**
 * Read all lines in a file, with line-endings normalized to `\n`. The result is [trim]-ed.
 *
 * This helps ensure consistent test results across Windows and Unix platforms.
 */
fun File.readTextNormalized() = useLines(Charsets.UTF_8) { it.joinToString("\n").trim() }
