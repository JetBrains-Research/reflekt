package io.reflekt.plugin.analysis.analyzer.source

import io.reflekt.plugin.analysis.processor.source.Processor
import io.reflekt.plugin.analysis.psi.visit
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

open class BaseAnalyzer(open val ktFiles: Set<KtFile>, open val binding: BindingContext, protected open val messageCollector: MessageCollector? = null) {
    protected fun KtFile.process(processors: Set<Processor<*>>) {
        messageCollector?.log("Start analyzing file ${this.name} (package ${this.packageFqName}")
        this.visit(processors)
        messageCollector?.log("Finish analyzing file ${this.name} (package ${this.packageFqName}")
    }
}
