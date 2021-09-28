package org.jetbrains.reflekt.plugin.analysis.processor.invokes

import org.jetbrains.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import org.jetbrains.reflekt.plugin.analysis.common.ReflektEntity
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.reflekt.plugin.analysis.processor.fullName
import org.jetbrains.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ClassInvokesProcessor(override val binding: BindingContext) : BaseInvokesProcessor<ClassOrObjectInvokes>(binding) {
    override val fileToInvokes: HashMap<FileID, ClassOrObjectInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, ClassOrObjectInvokes> {
        processClassOrObjectInvokes(element)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addAll(it)
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.CLASSES.fqName
}
