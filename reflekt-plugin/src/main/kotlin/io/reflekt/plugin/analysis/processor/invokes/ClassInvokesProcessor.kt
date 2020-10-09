package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

class ClassInvokesProcessor (override val binding: BindingContext): BaseInvokesProcessor<ClassOrObjectInvokes>(binding){
    override val invokes: ClassOrObjectInvokes = HashSet()

    override fun process(element: KtElement): ClassOrObjectInvokes {
        invokes.addAll(processClassOrObjectInvokes(element))
        return invokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektFqNames.CLASSES.fqName
}
