/**
 * Internal utilities used to work with files in reflekt
 */

package org.jetbrains.reflekt.util.file

import java.io.File

/**
 * Get all low-level files in subdirectories of `this` rootPath
 *
 * @param ignoredDirectories The content of directories with this name will be ignored
 * @return list with corresponding files
 */
fun String.getAllNestedFiles(
    ignoredDirectories: Set<String> = emptySet(),
): List<File> =
    File(this)
        .walkTopDown()
        .onEnter { it.name !in ignoredDirectories }
        .filter { it.isFile }
        .toList()

/**
 * Get all subdirectories of `this` rootPath
 *
 * @return list with corresponding directories
 */
fun String.getNestedDirectories(): List<File> =
    File(this).listFiles()?.filter { it.isDirectory } ?: emptyList()
