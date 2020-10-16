package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.*
import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.analysis.SubTypesToAnnotations

fun singleLineCode(format: String, vararg args: Any?): CodeBlock = CodeBlock.of("$format\n", *args)

fun notImplementedError(): CodeBlock = singleLineCode("error(%S)", "Not implemented")

fun Map<String, TypeName>.toParameterSpecs(): List<ParameterSpec> = entries.map { ParameterSpec(it.key, it.value) }

fun generateFunction(
    name: String,
    body: CodeBlock,
    typeVariables: List<TypeVariableName> = listOf(),
    arguments: List<ParameterSpec> = listOf(),
    returnType: TypeName? = null
): FunSpec =
    FunSpec.builder(name).generateBody(
        body = body,
        typeVariables = typeVariables,
        arguments = arguments,
        returnType = returnType
    )

private fun FunSpec.Builder.generateBody(
    body: CodeBlock,
    typeVariables: List<TypeVariableName> = listOf(),
    arguments: List<ParameterSpec> = listOf(),
    returnType: TypeName? = null
): FunSpec {
    addTypeVariables(typeVariables)
        .addParameters(arguments)
        .addCode(body)
    returnType?.let { returns(it) }
    return build()
}

fun addSuffix(str: String, suffix: String = ""): String = "${str}$suffix"


fun main(){
    val s1 = setOf("subTypeFqName1", "subTypeFqName2")
    val s2 = setOf("subTypeFqName3", "subTypeFqName4")

    val a1 = setOf("annotationFqName1", "annotationFqName2")
    val a2 = setOf("annotationFqName3", "annotationFqName4")

    val uN1 = listOf("fqName1", "fqName2")
    val uN2 = listOf("fqName3", "fqName4")

    val sToA3 = SubTypesToAnnotations(setOf("subTypeFqName3", "subTypeFqName4"), setOf())
    val sToA4 = SubTypesToAnnotations(setOf("subTypeFqName1", "subTypeFqName2"), setOf())

    val map1 = mutableMapOf(s1 to mapOf(a1 to uN1, emptySet<String>() to uN1), s2 to mapOf(a2 to uN1, emptySet<String>() to uN1))

 //   generateWhenBody(c, "fqNames", TypeVariableName("T"), "Unknown fully qualified name", "")

    // Subtypes
    val fqNames: Set<String> = setOf()
    when(fqNames) {
        setOf("subTypeFqName1", "subTypeFqName2") -> listOf("fqName1", "fqName2")
        setOf("subTypeFqName3", "subTypeFqName4") -> listOf("fqName3", "fqName4")
        else -> error("")
    }

    // Annotations
    val annotationFqNames: Set<String> = setOf()
    val subtypeFqNames: Set<String> = setOf()
    when(subtypeFqNames) {
        setOf("subTypeFqName1", "subTypeFqName2") -> {
            when (annotationFqNames) {
                setOf("annotationFqName1", "annotationFqName2") -> listOf("fqName1", "fqName2")
                else -> error("")
            }
        }
        setOf("subTypeFqName3", "subTypeFqName4") -> {
            when (annotationFqNames) {
                setOf("annotationFqName3", "annotationFqName4") -> listOf("fqName3", "fqName4")
                else -> error("")
            }
        }
        else -> error("")
    }
}
