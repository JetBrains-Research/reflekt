package io.reflekt.plugin.analysis.ir

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.utils.Util.log
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class ReflektIrUsesAnalyzer(
    private val invokes: ReflektInvokes,
    private val messageCollector: MessageCollector? = null
) : IrElementVisitor<Unit, Nothing?> {
    private val uses = IrReflektUses(
        objects = invokes.objects.associateWith { ArrayList() },
        classes = invokes.classes.associateWith { ArrayList() },
        functions = invokes.functions.associateWith { ArrayList() }
    )

    override fun visitClass(declaration: IrClass, data: Nothing?) {
        super.visitClass(declaration, data)

        if (declaration.visibility != DescriptorVisibilities.PUBLIC || declaration.modality == Modality.ABSTRACT) {
            return
        }

        val fqName = declaration.fqNameWhenAvailable?.asString() ?: return
        val subTypes = declaration.superTypes.plus(declaration.defaultType).mapNotNull { it.classFqName?.asString() }
        val annotations = declaration.annotations.mapNotNull { (it.type as? IrSimpleType)?.classFqName?.asString() }

        val (invokesType, usesType) = if (declaration.isObject) Pair(invokes.objects, uses.objects) else Pair(invokes.classes, uses.classes)

        invokesType.filter {
            it.covers(subTypes, annotations)
        }.forEach {
            usesType.getValue(it).add(fqName)
        }
    }

    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        super.visitFunction(declaration, data)

        if (declaration.visibility != DescriptorVisibilities.PUBLIC) {
            return
        }

        val fqName = declaration.fqNameWhenAvailable?.asString() ?: return
        val dispatchReceiver = declaration.dispatchReceiverParameter?.type
        val extensionReceiver = declaration.dispatchReceiverParameter?.type
        val isObjectReceiver = dispatchReceiver?.getClass()?.isObject ?: false || extensionReceiver?.getClass()?.isObject ?: false

        val receiverType = if (isObjectReceiver) {
            emptyList()
        } else {
            listOfNotNull(dispatchReceiver?.toParameterizedType(), extensionReceiver?.toParameterizedType())
        }
        val signatureTypes = receiverType.plus(declaration.valueParameters.map { it.type.toParameterizedType() })
        val signature = ParameterizedType(
            "kotlin.Function${signatureTypes.size}",
            signatureTypes.plus(declaration.returnType.toParameterizedType())
        )
        val annotations = declaration.annotations.mapNotNull { (it.type as? IrSimpleType)?.classFqName?.asString() }

        invokes.functions.filter {
            it.covers(signature, annotations)
        }.forEach {
            uses.functions.getValue(it).add(
                IrFunctionInfo(
                    fqName = fqName,
                    dispatchReceiverFqName = dispatchReceiver?.classFqName?.asString(),
                    extensionReceiverFqName = extensionReceiver?.classFqName?.asString(),
                    isObjectReceiver = isObjectReceiver
                )
            )
        }
    }

    override fun visitElement(element: IrElement, data: Nothing?) {
        element.acceptChildren(this, data)
    }

    private fun SubTypesToAnnotations.covers(classSubTypes: Iterable<String>, classAnnotations: Iterable<String>) =
        subTypes.intersect(classSubTypes).isNotEmpty()
            && (annotations.isEmpty() || annotations.intersect(classAnnotations).isNotEmpty())

    private fun SignatureToAnnotations.covers(functionSignature: ParameterizedType, functionAnnotations: Iterable<String>) =
        signature == functionSignature && (annotations.isEmpty() || annotations.intersect(functionAnnotations).isNotEmpty())

    companion object {
        fun collectUses(moduleFragment: IrModuleFragment, invokes: ReflektInvokes, messageCollector: MessageCollector? = null): IrReflektUses {
            val visitor = ReflektIrUsesAnalyzer(invokes, messageCollector)
            moduleFragment.accept(visitor, null)
            return visitor.uses
        }
    }
}
