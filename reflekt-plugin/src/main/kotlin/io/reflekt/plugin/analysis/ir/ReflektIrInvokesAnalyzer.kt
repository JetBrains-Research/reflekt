package io.reflekt.plugin.analysis.ir

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.generation.common.ReflektInvokeParts
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class ReflektIrInvokesAnalyzer(private val messageCollector: MessageCollector? = null) : IrElementVisitor<Unit, Nothing?> {
    private val invokes = ReflektInvokes()

    override fun visitCall(expression: IrCall, data: Nothing?) {
        val function = expression.symbol.owner
        val expressionFqName = function.fqNameForIrSerialization.toString()
        val invokeParts = ReflektInvokeParts.parse(expressionFqName) ?: return super.visitCall(expression, data)

        when (invokeParts.entityType) {
            ReflektEntity.OBJECTS, ReflektEntity.CLASSES -> {
                val invokeType = if (invokeParts.entityType == ReflektEntity.OBJECTS) invokes.objects else invokes.classes
                invokeType.add(ReflektInvokeArgumentsCollector.collectInvokeArguments(expression))
            }
            ReflektEntity.FUNCTIONS -> {
                invokes.functions.add(ReflektFunctionInvokeArgumentsCollector.collectInvokeArguments(expression))
            }
        }
    }

    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    companion object {
        fun collectInvokes(moduleFragment: IrModuleFragment, messageCollector: MessageCollector? = null): ReflektInvokes {
            val visitor = ReflektIrInvokesAnalyzer(messageCollector)
            moduleFragment.accept(visitor, null)
            return visitor.invokes
        }
    }
}
