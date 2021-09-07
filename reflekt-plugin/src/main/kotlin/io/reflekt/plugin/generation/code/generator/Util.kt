package io.reflekt.plugin.generation.code.generator

import com.squareup.kotlinpoet.*

fun statement(format: String, args: List<Any>): CodeBlock = statement(format, *args.toTypedArray())

fun statement(format: String, vararg args: Any?): CodeBlock = CodeBlock.builder().addStatement(format, *args).build()

fun controlFlow(code: CodeBlock, format: String, vararg args: Any?): CodeBlock =
    CodeBlock.builder().beginControlFlow(format, *args).add(code).endControlFlow().build()

fun wrappedCode(code: CodeBlock): CodeBlock = controlFlow(code, "{")

fun notImplementedError(): CodeBlock = statement("error(%S)", "Not implemented")

fun emptyListCode(): CodeBlock = statement("return emptyList()")

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
