package io.reflekt.util

import net.lingala.zip4j.ZipFile
import java.io.File

object FileUtil {

    fun extractAllFiles(zipFile: File): List<File> = getNestedFiles(unZipFile(zipFile))

    fun getNestedFiles(rootPath: String, files: MutableList<File> = ArrayList()): List<File> {
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
