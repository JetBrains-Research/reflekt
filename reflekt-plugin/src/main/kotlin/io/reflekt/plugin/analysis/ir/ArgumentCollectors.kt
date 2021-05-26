package io.reflekt.plugin.analysis.ir

import io.reflekt.plugin.analysis.common.ReflektFunction
import io.reflekt.plugin.analysis.models.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class ReflektInvokeArgumentsCollector : IrElementVisitor<Unit, Nothing?> {
    private val subtypes = HashSet<String>()
    private val annotations = HashSet<String>()

    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data)
        val function = expression.symbol.owner
        when (function.name.asString()) {
            ReflektFunction.WITH_SUBTYPE.functionName -> {
                subtypes.addAll(expression.getFqNamesOfTypeArguments())
            }
            ReflektFunction.WITH_SUBTYPES.functionName -> {
                subtypes.addAll(expression.getFqNamesOfClassReferenceValueArguments())
            }
            ReflektFunction.WITH_ANNOTATIONS.functionName -> {
                annotations.addAll(expression.getFqNamesOfClassReferenceValueArguments())
                subtypes.addAll(expression.getFqNamesOfTypeArguments())
            }
        }
    }

    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    companion object {
        fun collectInvokeArguments(expression: IrCall): SubTypesToAnnotations {
            val visitor = ReflektInvokeArgumentsCollector()
            expression.accept(visitor, null)
            return SubTypesToAnnotations(visitor.subtypes, visitor.annotations)
        }
    }
}

class ReflektFunctionInvokeArgumentsCollector : IrElementVisitor<Unit, Nothing?> {
    var signature: ParameterizedType? = null
    val annotations = HashSet<String>()

    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data)
        val function = expression.symbol.owner
        when (function.name.asString()) {
            ReflektFunction.WITH_ANNOTATIONS.functionName -> {
                annotations.addAll(expression.getFqNamesOfClassReferenceValueArguments())
                signature = expression.getTypeArgument(0)!!.toParameterizedType()
            }
        }
    }

    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    companion object {
        fun collectInvokeArguments(expression: IrCall): SignatureToAnnotations {
            val visitor = ReflektFunctionInvokeArgumentsCollector()
            expression.accept(visitor, null)
            return SignatureToAnnotations(visitor.signature!!, visitor.annotations)
        }
    }
}
