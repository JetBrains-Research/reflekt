package org.jetbrains.reflekt.plugin.analysis.analyzer.source

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.reflekt.plugin.analysis.processor.source.Processor
import org.jetbrains.reflekt.plugin.analysis.psi.visit
import org.jetbrains.reflekt.plugin.utils.Util.log

/**
 * @property ktFiles
 * @property binding
 * @property messageCollector
 */
open class BaseAnalyzer(
    open val ktFiles: Set<KtFile>,
    open val binding: BindingContext,
    protected open val messageCollector: MessageCollector? = null) {
    protected fun KtFile.process(processors: Set<Processor<*>>) {
        messageCollector?.log("Start analyzing file ${this.name} (package ${this.packageFqName})")
        this.visit(processors)
        messageCollector?.log("Finish analyzing file ${this.name} (package ${this.packageFqName})")
    }
}
