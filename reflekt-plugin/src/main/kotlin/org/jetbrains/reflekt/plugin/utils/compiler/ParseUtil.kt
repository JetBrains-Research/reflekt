package org.jetbrains.reflekt.plugin.utils.compiler

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.vfs.CharsetToolkit
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.psi.impl.PsiFileFactoryImpl
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * Get KtFile representation for set of files in specified environment
 *
 * @param files
 * @param environment
 * @return set of [KtFile] representations
 */
fun parseKtFiles(files: Collection<File>, environment: KotlinCoreEnvironment): Set<KtFile> {
    val factory: PsiFileFactoryImpl = PsiFileFactory.getInstance(environment.project) as PsiFileFactoryImpl
    return files.mapNotNull { file ->
        val virtualFile = KotlinLightVirtualFile(file, file.readText()).apply {
            charset = CharsetToolkit.UTF8_CHARSET
        }
        factory.trySetupPsiForFile(virtualFile, KotlinLanguage.INSTANCE, true, false) as? KtFile
    }.toSet()
}
