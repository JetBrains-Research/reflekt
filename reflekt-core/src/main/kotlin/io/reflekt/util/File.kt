package io.reflekt.plugin.util

import net.lingala.zip4j.ZipFile
import org.jetbrains.kotlin.incremental.isKotlinFile
import java.io.File

object FileUtil {

    fun extractAllFiles(zipFile: File): List<File> = getNestedFiles(unZipFile(zipFile))

    private fun List<File>.toKotlinFilesSet(extensions: List<String>): Set<File> = this.filter { it.isKotlinFile(sourceFilesExtensions = extensions) }.toSet()

    fun Set<File>.toKotlinFilesSet(extensions: List<String>): Set<File> = this.toList().toKotlinFilesSet(extensions)

    private fun getNestedFiles(rootPath: String, files: MutableList<File> = ArrayList()): List<File> {
        val root = File(rootPath)
        root.listFiles()?.forEach {
            if (it.isFile) {
                files.add(it)
            } else if (it.isDirectory) {
                getNestedFiles(it.absolutePath, files)
            }
        }
        return files
    }

    private fun unZipFile(file: File, destinationPath: String? = null): String {
        val zipFile = ZipFile(file.path)
        val outputPath = destinationPath ?: file.parent
        zipFile.extractAll(outputPath)
        return outputPath
    }
}
