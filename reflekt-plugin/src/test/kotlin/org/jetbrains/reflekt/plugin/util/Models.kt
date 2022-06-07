package org.jetbrains.reflekt.plugin.util

import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.reflekt.plugin.analysis.ir.receiverType
import org.jetbrains.reflekt.plugin.analysis.models.SignatureToAnnotations
import org.jetbrains.reflekt.plugin.analysis.models.SupertypesToAnnotations
import org.jetbrains.reflekt.plugin.analysis.processor.FileId

fun IrType?.toPrettyString() = (this?.classifierOrNull?.owner as IrDeclarationWithName).name.asString()

fun IrFunction.toPrettyString(): String {
    val receiver = receiverType()?.let { "${it.toPrettyString()}." } ?: ""
    return "$receiver$name"
}

fun KtNamedDeclaration.toPrettyString() = fqName.toString()

inline fun <T : Any> Pair<FileId, T>.toPrettyString(transform: (T) -> CharSequence) = "file: ${this.first}: ${transform(this.second)}"

fun SupertypesToAnnotations.toPrettyString() = "supertypesToAnnotations: ${
    listOf(
        "supertypes: ${supertypes.joinToStringIndented()},\n" +
            "annotations: ${annotations.joinToStringIndented()}"
    ).joinToStringIndented()
}"

fun SignatureToAnnotations.toPrettyString() = "signatureToAnnotations: ${
    listOf(
        "irSignature: ${irSignature.toPrettyString()},\n" +
            "annotations: ${annotations.joinToStringIndented()}"
    ).joinToStringIndented()
}"
