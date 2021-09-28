package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.plugin.util.Util
import org.jetbrains.reflekt.util.FileUtil
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

fun getProjectFilesInDirectory(directory: File): Set<File> {
    return FileUtil.getAllNestedFiles(directory.findInDirectory("project", true).absolutePath, ignoredDirectories = setOf(".idea")).toSet()
}

fun getTestsDirectories(cls: KClass<*>): List<File> {
    return FileUtil.getNestedDirectories(Util.getResourcesRootPath(cls)).sorted().filter { "test" in it.name }
}
