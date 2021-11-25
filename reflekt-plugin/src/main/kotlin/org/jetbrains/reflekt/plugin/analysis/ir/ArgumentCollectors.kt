@file:Suppress("FILE_WILDCARD_IMPORTS")

package org.jetbrains.reflekt.plugin.analysis.ir

import org.jetbrains.reflekt.plugin.analysis.common.*
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.psi.SignatureToAnnotations
import org.jetbrains.reflekt.plugin.analysis.models.psi.SupertypesToAnnotations

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.types.KotlinType

open class IrRecursiveVisitor : IrElementVisitor<Unit, Nothing?> {
    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }
}

/**
 * IR version of [findReflektInvokeArguments] function.
 * Traverses subtree of expression and collects arguments of withSupertype, withSupertypes and withAnnotations calls to construct [SupertypesToAnnotations].
 */
class ReflektInvokeArgumentsCollector : IrRecursiveVisitor() {
    private val supertypes = HashSet<String>()
    private val annotations = HashSet<String>()

    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data)
        val function = expression.symbol.owner
        when (function.name.asString()) {
            ReflektFunction.WITH_SUPERTYPE.functionName -> supertypes.addAll(expression.getFqNamesOfTypeArguments())
            ReflektFunction.WITH_SUPERTYPES.functionName -> supertypes.addAll(expression.getFqNamesOfClassReferenceValueArguments())
            ReflektFunction.WITH_ANNOTATIONS.functionName -> {
                annotations.addAll(expression.getFqNamesOfClassReferenceValueArguments())
                supertypes.addAll(expression.getFqNamesOfTypeArguments())
            }
        }
    }

    companion object {
        fun collectInvokeArguments(expression: IrCall): SupertypesToAnnotations? {
            val visitor = ReflektInvokeArgumentsCollector()
            expression.accept(visitor, null)
            if (visitor.supertypes.isEmpty()) {
                return null
            }
            return SupertypesToAnnotations(visitor.supertypes, visitor.annotations)
        }
    }
}

/**
 * IR version of [findReflektFunctionInvokeArguments] function.
 * Traverses subtree of expression and collects arguments of withSupertype, withSupertypes and withAnnotations calls to construct [SignatureToAnnotations].
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

/**
 * IR version of [findSmartReflektInvokeArguments] function.
 * Traverses subtree of expression and collects arguments of filter calls to construct [TypeArgumentToFilters].
 */
class SmartReflektInvokeArgumentsCollector(private val sourceFile: SourceFile) : IrRecursiveVisitor() {
    private var typeArgument: KotlinType? = null
    private var typeArgumentFqName: String? = null
    private val filters = ArrayList<Lambda>()

    @ObsoleteDescriptorBasedAPI
    override fun visitCall(expression: IrCall, data: Nothing?) {
        super.visitCall(expression, data)
        val function = expression.symbol.owner
        if (function.name.asString() in ReflektEntity.values().map { it.entityType }) {
            val typeArgument = expression.getTypeArgument(0)
            this.typeArgument = typeArgument?.toParameterizedType()
            typeArgumentFqName = typeArgument?.classFqName?.asString()
        }
    }

    @Suppress("AVOID_NULL_CHECKS")
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
        fun collectInvokeArguments(expression: IrCall, sourceFile: SourceFile): TypeArgumentToFilters {
            val visitor = SmartReflektInvokeArgumentsCollector(sourceFile)
            expression.accept(visitor, null)
            return TypeArgumentToFilters(
                typeArgument = visitor.typeArgument,
                typeArgumentFqName = visitor.typeArgumentFqName,
                filters = visitor.filters,
                imports = sourceFile.imports,
            )
        }
    }
}
