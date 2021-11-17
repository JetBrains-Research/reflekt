/**
 * Internal utilities used to work with files in reflekt
 */

package org.jetbrains.reflekt.util.file

import net.lingala.zip4j.ZipFile
import java.io.File

/**
 * Unzip `this` file and get all low-level files in subdirectories
 *
 * @return list with corresponding files
 */
fun File.extractAllFiles(): List<File> = this.unZipFile().getAllNestedFiles()

/**
 * Get all low-level files in subdirectories of `this` rootPath
 *
 * @param files
 * @param ignoredDirectories
 * @return list with corresponding files
 */
fun String.getAllNestedFiles(
    files: MutableList<File> = ArrayList(),
    ignoredDirectories: Set<String> = emptySet(),
): List<File> {
    val root = File(this)
    root.listFiles()?.forEach {
        if (it.isFile) {
            files.add(it)
        } else if (it.isDirectory && it.name !in ignoredDirectories) {
            it.absolutePath.getAllNestedFiles(files)
        }
    }
    return files
}

/**
 * Get all subdirectories of `this` rootPath
 *
 * @return list with corresponding directories
 */
fun String.getNestedDirectories() = File(this).listFiles()?.filter { it.isDirectory } ?: emptyList()

private fun File.unZipFile(destinationPath: String? = null): String {
    val zipFile = ZipFile(this.path)
    val outputPath = destinationPath ?: this.parent
    zipFile.extractAll(outputPath)
    return outputPath
}
