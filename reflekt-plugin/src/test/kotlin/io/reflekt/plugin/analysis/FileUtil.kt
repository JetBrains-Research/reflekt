package io.reflekt.plugin.analysis

import io.reflekt.plugin.util.Util
import io.reflekt.util.file.getAllNestedFiles
import io.reflekt.util.file.getNestedDirectories
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

fun getProjectFilesInDirectory(directory: File): Set<File> = directory.findInDirectory("project", true).absolutePath.getAllNestedFiles(ignoredDirectories = setOf(".idea"))
    .toSet()

fun getTestsDirectories(cls: KClass<*>): List<File> = Util.getResourcesRootPath(cls).getNestedDirectories().sorted()
    .filter { "test" in it.name }
