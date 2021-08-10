package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.psi.function.fqName
import io.reflekt.plugin.util.Util
import io.reflekt.util.FileUtil
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.types.KotlinType

//  We cannot use Json to store and test KotlinType (subType), so we build a string representation, sufficient to test it.
//  We also want to check fqName of subtype, which is not included in its toString(), so we added it separately.
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

fun SubTypesToAnnotations.toPrettyString(): String {
    return "subTypes: ${subTypes.joinToStringIndented()},\n" +
        "annotations: ${annotations.joinToStringIndented()}"
}

@JvmName("toPrettyStringClassOrObjectInvokes")
fun ClassOrObjectInvokes.toPrettyString(): String {
    return joinToStringIndented { it.toPrettyString() }
}

fun SubTypesToFilters.toPrettyString(): String {
    return "subtype: ${subType?.toPrettyString()},\n" +
        "filters: ${filters.joinToStringIndented()},\n" +
        "imports: ${imports.joinToStringIndented()}"
}

@JvmName("toPrettyStringSubTypesToFilters")
fun Set<SubTypesToFilters>.toPrettyString(): String {
    return joinToStringIndented { it.toPrettyString() }
}

fun ReflektInvokes.toPrettyString(): String {
    return "objects: ${objects.toPrettyString()},\n" +
        "classes: ${classes.toPrettyString()},\n" +
        "functions: ${functions.toPrettyString()}"
}

@JvmName("toPrettyStringClassOrObjectUses")
fun ClassOrObjectUses.toPrettyString(): String {
    return joinToStringIndented { subTypesToAnnotations, classOrObjectList ->
        "subTypesToAnnotations: ${listOf(subTypesToAnnotations.toPrettyString()).joinToStringIndented()},\n" +
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
