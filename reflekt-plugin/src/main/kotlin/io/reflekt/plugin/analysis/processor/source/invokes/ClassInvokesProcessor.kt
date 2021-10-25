package io.reflekt.plugin.analysis.processor.source.invokes

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.processor.FileID
import io.reflekt.plugin.analysis.processor.fullName
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ClassInvokesProcessor(override val binding: BindingContext, override val messageCollector: MessageCollector?) :
    BaseInvokesProcessor<ClassOrObjectInvokes>(binding, messageCollector) {
    override val fileToInvokes: HashMap<FileID, ClassOrObjectInvokes> = HashMap()

    override fun process(element: KtElement, file: KtFile): HashMap<FileID, ClassOrObjectInvokes> {
        processClassOrObjectInvokes(element)?.let {
            fileToInvokes.getOrPut(file.fullName) { HashSet() }.addAll(it)
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektEntity.CLASSES.fqName
}
