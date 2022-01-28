package org.jetbrains.reflekt.plugin.generation.code.generator

import com.squareup.kotlinpoet.*

/**
 * Convert a map with variables with their [types](TypeName) into a list of parameter declarations
 *
 * @return a list of [ParameterSpec]
 */
fun Map<String, TypeName>.toParameterSpecs(): List<ParameterSpec> = entries.map { ParameterSpec(it.key, it.value) }

/**
 * Generate function's body
 *
 * @param body function's body
 * @param typeVariables if the function has generic variables,specify all of them with their names and bounds
 * @param arguments function's arguments
 * @param returnType function's return type
 */
private fun FunSpec.Builder.generateBody(
    body: CodeBlock,
    typeVariables: List<TypeVariableName> = listOf(),
    arguments: List<ParameterSpec> = listOf(),
    returnType: TypeName? = null,
): FunSpec {
    addTypeVariables(typeVariables)
        .addParameters(arguments)
        .addCode(body)
    returnType?.let { returns(it) }
    return build()
}

/**
 * Create a [CodeBlock] statement by it's template and a list of arguments
 *
 * @param template template of the statement
 * @param args arguments for the [template]
 * @return [CodeBlock] for the statement
 */
fun statement(template: String, vararg args: Any?): CodeBlock = CodeBlock.builder().addStatement(template, *args).build()

/**
 * Create new [CodeBlock] inside of the [template]. If the template has an opening brace,
 *  an closing brace will be added automatically
 *
 * @param code [CodeBlock] that should be wrapped
 * @param template
 * @param args arguments for the [template]
 * @return [CodeBlock] for the controlFlow
 */
fun controlFlow(
    code: CodeBlock,
    template: String,
    vararg args: Any?): CodeBlock =
    CodeBlock.builder().beginControlFlow(template, *args).add(code)
        .endControlFlow().build()

/**
 * Wrap [CodeBlock] into brackets
 *
 * @param code [CodeBlock] that should be wrapped
 * @return a wrapped [CodeBlock]
 */
fun wrappedCode(code: CodeBlock): CodeBlock = controlFlow(code, "{")

/**
 * Create [CodeBlock] with empty list
 *
 * @return a [CodeBlock] with {@code return emptyList()} statement
 */
fun emptyListCode(): CodeBlock = statement("return emptyList()")

/**
 * The functions for the generating a Kotlin function with its [name], [body], [arguments] and [returnType]
 *
 * @param name name of the function
 * @param body function's body
 * @param typeVariables if the function has generic variables,specify all of them with their names and bounds
 * @param arguments function's arguments
 * @param returnType function's return type
 * @return a generated function declaration
 */
fun generateFunction(
    name: String,
    body: CodeBlock,
    typeVariables: List<TypeVariableName> = listOf(),
    arguments: List<ParameterSpec> = listOf(),
    returnType: TypeName? = null,
): FunSpec =
    FunSpec.builder(name).generateBody(
        body = body,
        typeVariables = typeVariables,
        arguments = arguments,
        returnType = returnType,
    )
