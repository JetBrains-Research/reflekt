package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.*

fun singleLineCode(format: String, vararg args: Any?): CodeBlock
    = CodeBlock.of("$format\n", *args)

fun Map<String, TypeName>.toParameterSpecs(): List<ParameterSpec>
    = entries.map { ParameterSpec(it.key, it.value) }

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
