package io.reflekt.plugin.analysis.ir

import io.reflekt.plugin.analysis.common.ReflektEntity
import io.reflekt.plugin.analysis.common.ReflektFunction
import io.reflekt.plugin.analysis.models.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.types.KotlinType

open class IrRecursiveVisitor : IrElementVisitor<Unit, Nothing?> {
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }
}

/* IR version of findReflektInvokeArguments function.
 * Traverses subtree of expression and collects arguments of withSubtype, withSubtypes and withAnnotations calls to construct SubTypesToAnnotations.
 */
class ReflektInvokeArgumentsCollector : IrRecursiveVisitor() {
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

    companion object {
        fun collectInvokeArguments(expression: IrCall): SubTypesToAnnotations? {
            val visitor = ReflektInvokeArgumentsCollector()
            expression.accept(visitor, null)
            if (visitor.subtypes.isEmpty()) {
                return null
            }
            return SubTypesToAnnotations(visitor.subtypes, visitor.annotations)
        }
    }
}

/* IR version of findReflektFunctionInvokeArguments function.
 * Traverses subtree of expression and collects arguments of withSubtype, withSubtypes and withAnnotations calls to construct SignatureToAnnotations.
 */
class ReflektFunctionInvokeArgumentsCollector : IrRecursiveVisitor() {
    private var signature: KotlinType? = null
    private val annotations = HashSet<String>()

    @ObsoleteDescriptorBasedAPI
    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data)
        val function = expression.symbol.owner
        when (function.name.asString()) {
            ReflektFunction.WITH_ANNOTATIONS.functionName -> {
                annotations.addAll(expression.getFqNamesOfClassReferenceValueArguments())
                signature = expression.getTypeArgument(0)?.toParameterizedType()
            }
        }
    }

    companion object {
        fun collectInvokeArguments(expression: IrCall): SignatureToAnnotations? {
            val visitor = ReflektFunctionInvokeArgumentsCollector()
            expression.accept(visitor, null)
            return visitor.signature?.let { SignatureToAnnotations(it, visitor.annotations) }
        }
    }
}

/* IR version of findSmartReflektInvokeArguments function.
 * Traverses subtree of expression and collects arguments of filter calls to construct SubTypesToFilters.
 */
class SmartReflektInvokeArgumentsCollector(private val sourceFile: SourceFile) : IrRecursiveVisitor() {
    private var subtype: KotlinType? = null
    private val filters = ArrayList<Lambda>()

    @ObsoleteDescriptorBasedAPI
    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data)
        val function = expression.symbol.owner
        if (function.name.asString() in ReflektEntity.values().map { it.entityType }) {
            subtype = expression.getTypeArgument(0)?.toParameterizedType()
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Nothing?) {
        val function = expression.function
        if (function.body == null) {
            return
        }
        val body = sourceFile.content.substring(function.body!!.startOffset, function.body!!.endOffset)
        val parameters = function.valueParameters.map { it.name.toString() }
        filters.add(Lambda(body = body, parameters = parameters))

        super.visitFunctionExpression(expression, data)
    }

    companion object {
        fun collectInvokeArguments(expression: IrCall, sourceFile: SourceFile): SubTypesToFilters {
            val visitor = SmartReflektInvokeArgumentsCollector(sourceFile)
            expression.accept(visitor, null)
            return SubTypesToFilters(
                subType = visitor.subtype,
                filters = visitor.filters,
                imports = sourceFile.imports
            )
        }
    }
}
