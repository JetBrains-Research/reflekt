package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.Reflekt
import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

abstract class BaseInvokesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    abstract val invokes: Output

    protected enum class ReflektNames(val fqName: String){
        OBJECTS(Reflekt.Objects::class.qualifiedName!!),
        CLASSES(Reflekt.Classes::class.qualifiedName!!),
        FUNCTIONS(Reflekt.Functions::class.qualifiedName!!)
    }

    protected abstract fun isValidExpression(expression: KtReferenceExpression): Boolean

    override fun shouldRunOn(element: KtElement) = (element as? KtReferenceExpression)?.let{ isValidExpression(it) } ?: false
}
