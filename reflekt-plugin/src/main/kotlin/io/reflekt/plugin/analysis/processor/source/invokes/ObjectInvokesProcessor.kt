package io.reflekt.plugin.analysis.processor.source.invokes

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.processor.FileId
import io.reflekt.plugin.analysis.processor.fullName
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * @property binding
 * @property messageCollector
 */
class ObjectInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseInvokesProcessor<ClassOrObjectInvokes>(binding, messageCollector) {
    override val fileToInvokes: HashMap<FileId, ClassOrObjectInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileId, ClassOrObjectInvokes> {
        processClassOrObjectInvokes(element)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addAll(it)
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.OBJECTS.fqName
}
