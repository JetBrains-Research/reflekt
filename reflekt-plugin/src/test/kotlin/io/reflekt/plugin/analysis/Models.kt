package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.psi.function.fqName
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.types.KotlinType

//  We cannot use Json to store and test KotlinType (supertype), so we build a string representation, sufficient to test it.
//  We also want to check fqName of supertype, which is not included in its toString(), so we added it separately.
fun KotlinType?.toPrettyString() = "$this (${this?.fqName()})"

fun Collection<KotlinType?>.toPrettyString() = joinToStringIndented { it.toPrettyString() }

fun KtNamedDeclaration.toPrettyString() = fqName.toString()

fun SignatureToAnnotations.toPrettyString(): String {
    return "signature: ${signature.toPrettyString()},\n" +
        "annotations: ${annotations.joinToStringIndented()}"
}

@JvmName("toPrettyStringSFunctionInvokes")
fun FunctionInvokes.toPrettyString(): String {
    return joinToStringIndented { it.toPrettyString() }
}

fun SupertypesToAnnotations.toPrettyString(): String {
    return "supertypes: ${supertypes.joinToStringIndented()},\n" +
        "annotations: ${annotations.joinToStringIndented()}"
}

@JvmName("toPrettyStringClassOrObjectInvokes")
fun ClassOrObjectInvokes.toPrettyString(): String {
    return joinToStringIndented { it.toPrettyString() }
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

fun ReflektInvokes.toPrettyString(): String {
    return "objects: ${objects.toPrettyString()},\n" +
        "classes: ${classes.toPrettyString()},\n" +
        "functions: ${functions.toPrettyString()}"
}

@JvmName("toPrettyStringClassOrObjectUses")
fun ClassOrObjectUses.toPrettyString(): String {
    return joinToStringIndented { supertypesToAnnotations, classOrObjectList ->
        "supertypesToAnnotations: ${listOf(supertypesToAnnotations.toPrettyString()).joinToStringIndented()},\n" +
            "objectsOrClasses: ${classOrObjectList.joinToStringIndented { it.toPrettyString() }}"
    }
}

fun FunctionUses.toPrettyString(): String {
    return joinToStringIndented { signatureToAnnotations, namedFunctionList ->
        "signatureToAnnotations: ${listOf(signatureToAnnotations.toPrettyString()).joinToStringIndented()},\n" +
            "namedFunctions: ${namedFunctionList.joinToStringIndented { it.toPrettyString() }}"
    }
}

fun ReflektUses.toPrettyString(): String {
    return "objects: ${objects.toPrettyString()},\n" +
        "classes: ${classes.toPrettyString()},\n" +
        "functions: ${functions.toPrettyString()}"
}
