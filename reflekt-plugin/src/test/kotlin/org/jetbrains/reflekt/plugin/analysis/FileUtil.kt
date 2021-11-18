package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.util.file.getAllNestedFiles
import org.jetbrains.reflekt.util.file.getNestedDirectories
import org.jetbrains.reflekt.plugin.util.Util
import java.io.File
import kotlin.reflect.KClass

fun File.findInDirectory(name: String, toCreateIfDoesNotExist: Boolean = false): File {
    if (!this.isDirectory) {
        error("${this.absolutePath} is not a directory")
    }
    val baseErrorMessage = "in the directory ${this.name} was not found"
    return this.listFiles()?.firstOrNull { it.name == name } ?: run {
        if (toCreateIfDoesNotExist) {
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
