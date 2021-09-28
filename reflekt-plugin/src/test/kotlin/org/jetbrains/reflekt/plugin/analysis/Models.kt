package org.jetbrains.reflekt.plugin.analysis

import org.jetbrains.reflekt.plugin.analysis.models.*
import org.jetbrains.reflekt.plugin.analysis.psi.function.shortFqName
import org.jetbrains.reflekt.plugin.analysis.processor.FileID
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.types.KotlinType

//  We cannot use Json to store and test KotlinType (supertype), so we build a string representation, sufficient to test it.
//  We also want to check fqName of supertype, which is not included in its toString(), so we added it separately.
fun KotlinType?.toPrettyString() = "$this (${this?.shortFqName()})"

fun KtNamedDeclaration.toPrettyString() = fqName.toString()

fun <T : Any> Pair<FileID, T>.toPrettyString(transform: (T) -> CharSequence): String {
    return "file: ${this.first}: ${transform(this.second)}"
}

fun SupertypesToAnnotations.toPrettyString(): String {
    return "supertypesToAnnotations: ${
        listOf(
            "supertypes: ${supertypes.joinToStringIndented()},\n" +
                "annotations: ${annotations.joinToStringIndented()}"
        ).joinToStringIndented()
    }"
}

fun SignatureToAnnotations.toPrettyString(): String {
    return "signatureToAnnotations: ${
        listOf(
            "signature: ${signature.toPrettyString()},\n" +
            "annotations: ${annotations.joinToStringIndented()}"
        ).joinToStringIndented()
    }"
}

fun SupertypesToFilters.toPrettyString(): String {
    return "supertype: ${supertype?.toPrettyString()},\n" +
        "filters: ${filters.joinToStringIndented()},\n" +
        "imports: ${imports.joinToStringIndented()}"
}


@JvmName("toPrettyStringSupertypesToFilters")
fun Set<SupertypesToFilters>.toPrettyString(): String {
    return joinToStringIndented { it.toPrettyString() }
}

@JvmName("toPrettyStringSFunctionInvokes")
fun FunctionInvokes.toPrettyString(): String {
    return joinToStringIndented { it.toPrettyString() }
}

@JvmName("toPrettyStringClassOrObjectInvokes")
fun ClassOrObjectInvokes.toPrettyString(): String {
    return joinToStringIndented { it.toPrettyString() }
}


fun ReflektInvokes.toPrettyString(): String {
    return "objects: ${objects.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
        "classes: ${classes.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
        "functions: ${functions.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }}"
}

@JvmName("toPrettyStringClassOrObjectUses")
fun ClassOrObjectUses.toPrettyString(): String {
    return joinToStringIndented { supertypesToAnnotations, classOrObjectList ->
        "${supertypesToAnnotations.toPrettyString()},\n" +
            "objectsOrClasses: ${classOrObjectList.joinToStringIndented { it.toPrettyString() }}"
    }
}

fun FunctionUses.toPrettyString(): String {
    return joinToStringIndented { signatureToAnnotations, namedFunctionList ->
        "${signatureToAnnotations.toPrettyString()},\n" +
            "namedFunctions: ${namedFunctionList.joinToStringIndented { it.toPrettyString() }}"
    }
}

fun ReflektUses.toPrettyString(): String {
    return "objects: ${objects.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
        "classes: ${classes.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }},\n" +
        "functions: ${functions.joinToStringIndented { k, v -> (k to v).toPrettyString { v.toPrettyString() } }}"
}
