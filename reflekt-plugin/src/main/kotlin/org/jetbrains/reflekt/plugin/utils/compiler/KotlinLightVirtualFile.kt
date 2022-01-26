package org.jetbrains.reflekt.plugin.utils.compiler

import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import java.io.File

/**
 * Wrapper for [LightVirtualFile] that retains path on machine
 *
 * @param file a [File] that can be wrapped
 * @param text text that should be stored in the file
 * */
class KotlinLightVirtualFile(file: File, text: String) : LightVirtualFile(
    file.name,
    KotlinLanguage.INSTANCE,
    text,
) {
    private val path = file.canonicalPath

    override fun getPath(): String = path
}
