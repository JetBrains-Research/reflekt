package org.jetbrains.reflekt.plugin.analysis.collector.ir

import org.jetbrains.reflekt.plugin.analysis.analyzer.ir.IrAnalyzer

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.visitors.*

/**
 * A base class for collecting information for [analyzer], e.g. collect all instances
 * @property analyzer
 * @property messageCollector
 */
abstract class BaseCollector(
    protected val analyzer: IrAnalyzer,
    protected val messageCollector: MessageCollector? = null,
) : IrElementVisitorVoid {
    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }
}

/**
 * A class to accept several [collectors] by one IR traversal
 */
class IrComposedCollector(private val collectors: List<BaseCollector>) : IrElementVisitorVoid {
    override fun visitElement(element: IrElement) {
        collectors.forEach { element.acceptVoid(it) }
    }
}
