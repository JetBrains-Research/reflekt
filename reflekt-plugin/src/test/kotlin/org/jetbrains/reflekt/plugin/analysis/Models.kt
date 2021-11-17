package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.processor.FileId
import org.jetbrains.reflekt.plugin.analysis.psi.function.shortFqName

import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.types.KotlinType

// We cannot use Json to store and test KotlinType (supertype), so we build a string representation, sufficient to test it.
// We also want to check fqName of supertype, which is not included in its toString(), so we added it separately.
fun KotlinType?.toPrettyString() = "$this (${this?.shortFqName()})"

fun Collection<KotlinType?>.toPrettyString() = joinToStringIndented { it.toPrettyString() }

fun KtNamedDeclaration.toPrettyString() = fqName.toString()

fun SignatureToAnnotations.toPrettyString(): String = "signature: ${signature.toPrettyString()},\n" +
    "annotations: ${annotations.joinToStringIndented()}"

@JvmName("toPrettyStringSFunctionInvokes")
fun FunctionInvokes.toPrettyString(): String = joinToStringIndented { it.toPrettyString() }

fun SupertypesToAnnotations.toPrettyString(): String = "supertypes: ${supertypes.joinToStringIndented()},\n" +
    "annotations: ${annotations.joinToStringIndented()}"

@JvmName("toPrettyStringClassOrObjectInvokes")
fun ClassOrObjectInvokes.toPrettyString(): String = joinToStringIndented { it.toPrettyString() }

fun SupertypesToFilters.toPrettyString(): String = "supertype: ${supertype?.toPrettyString()},\n" +
    "filters: ${filters.joinToStringIndented()},\n" +
    "imports: ${imports.joinToStringIndented()}"

@JvmName("toPrettyStringSupertypesToFilters")
fun Set<SupertypesToFilters>.toPrettyString(): String = joinToStringIndented { it.toPrettyString() }

@JvmName("toPrettyStringSupertypesToFiltersWithReflektInvokes")
fun <T> HashMap<FileId, MutableSet<T>>.toPrettyString(toPrettyStringForT: (T) -> String): String = this.joinToStringIndented { k, v ->
    "file: $k: ${v.joinToStringIndented { toPrettyStringForT(it) }}"
}

fun ReflektInvokes.toPrettyString(): String = "objects: ${objects.toPrettyString(SupertypesToAnnotations::toPrettyString)},\n" +
    "classes: ${classes.toPrettyString(SupertypesToAnnotations::toPrettyString)},\n" +
    "functions: ${functions.toPrettyString(SignatureToAnnotations::toPrettyString)}"

@JvmName("toPrettyStringClassOrObjectUses")
fun ClassOrObjectUses.toPrettyString(): String = joinToStringIndented { supertypesToAnnotations, classOrObjectList ->
    "supertypesToAnnotations: ${listOf(supertypesToAnnotations.toPrettyString()).joinToStringIndented()},\n" +
        "objectsOrClasses: ${classOrObjectList.joinToStringIndented { it.toPrettyString() }}"
}

fun FunctionUses.toPrettyString(): String = joinToStringIndented { signatureToAnnotations, namedFunctionList ->
    "signatureToAnnotations: ${listOf(signatureToAnnotations.toPrettyString()).joinToStringIndented()},\n" +
        "namedFunctions: ${namedFunctionList.joinToStringIndented { it.toPrettyString() }}"
}

@JvmName("toPrettyStringSupertypesToFiltersWithReflektUses")
fun <T> HashMap<FileId, T>.toPrettyString(toPrettyStringForT: (T) -> String): String = this.joinToStringIndented { k, v ->
    "file: $k: ${toPrettyStringForT(v)}"
}

fun ReflektUses.toPrettyString(): String = "objects: ${objects.toPrettyString(ClassOrObjectUses::toPrettyString)},\n" +
    "classes: ${classes.toPrettyString(ClassOrObjectUses::toPrettyString)},\n" +
    "functions: ${functions.toPrettyString(FunctionUses::toPrettyString)}"
