package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.kotlin.ir.declarations.IrDeclarationWithName
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.models.psi.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.psi.function.shortFqName

//  We cannot use Json to store and test KotlinType (supertype), so we build a string representation, sufficient to test it.
//  We also want to check fqName of supertype, which is not included in its toString(), so we added it separately.
fun KotlinType?.toPrettyString() = "$this (${this?.shortFqName()})"

// todo: check it works
fun IrType?.toPrettyString() = (this?.classifierOrNull?.owner as IrDeclarationWithName).name.asString()

fun KtNamedDeclaration.toPrettyString() = fqName.toString()

fun <T : Any> Pair<FileId, T>.toPrettyString(transform: (T) -> CharSequence) = "file: ${this.first}: ${transform(this.second)}"

fun SupertypesToAnnotations.toPrettyString() = "supertypesToAnnotations: ${
    listOf(
        "supertypes: ${supertypes.joinToStringIndented()},\n" +
            "annotations: ${annotations.joinToStringIndented()}"
    ).joinToStringIndented()
}"

fun SignatureToAnnotations.toPrettyString() = "signatureToAnnotations: ${
    listOf(
        "signature: ${signature.toPrettyString()},\n" +
            "annotations: ${annotations.joinToStringIndented()}"
    ).joinToStringIndented()
}"

fun SupertypesToFilters.toPrettyString() = "supertype: ${supertype?.toPrettyString()},\n" +
    "filters: ${filters.joinToStringIndented()},\n" +
    "imports: ${imports.joinToStringIndented()}"


@JvmName("toPrettyStringSupertypesToFilters")
fun Set<SupertypesToFilters>.toPrettyString() = joinToStringIndented { it.toPrettyString() }

@JvmName("toPrettyStringSFunctionInvokes")
fun FunctionInvokes.toPrettyString() = joinToStringIndented { it.toPrettyString() }

@JvmName("toPrettyStringClassOrObjectInvokes")
fun ClassOrObjectInvokes.toPrettyString() = joinToStringIndented { it.toPrettyString() }


fun ReflektInvokes.toPrettyString() = "objects: ${objects.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
    "classes: ${classes.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
    "functions: ${functions.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }}"

@JvmName("toPrettyStringClassOrObjectUses")
fun ClassOrObjectUses.toPrettyString() = joinToStringIndented { supertypesToAnnotations, classOrObjectList ->
    "${supertypesToAnnotations.toPrettyString()},\n" +
        "objectsOrClasses: ${classOrObjectList.joinToStringIndented { it.toPrettyString() }}"
}

fun FunctionUses.toPrettyString() = joinToStringIndented { signatureToAnnotations, namedFunctionList ->
    "${signatureToAnnotations.toPrettyString()},\n" +
        "namedFunctions: ${namedFunctionList.joinToStringIndented { it.toPrettyString() }}"
}

fun ReflektUses.toPrettyString() = "objects: ${objects.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
    "classes: ${classes.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
    "functions: ${functions.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }}"
