package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.processor.fullName
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ClassInvokesProcessor(override val binding: BindingContext) : BaseInvokesProcessor<ClassOrObjectInvokes>(binding) {
    override val fileToInvokes: HashMap<String, ClassOrObjectInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<String, ClassOrObjectInvokes> {
        processClassOrObjectInvokes(element)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addAll(it)
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.CLASSES.fqName
}
