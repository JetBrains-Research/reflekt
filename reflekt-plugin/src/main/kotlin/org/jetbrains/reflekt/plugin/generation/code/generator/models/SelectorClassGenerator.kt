package org.jetbrains.reflekt.plugin.generation.code.generator.models

import org.jetbrains.reflekt.plugin.generation.code.generator.*

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

import kotlin.reflect.KClass

/**
 * An abstract class to generate a new internal class from the DSL e.g., WithSuperTypes or WithAnnotations.
 *
 * @property typeVariable a generic variable to parametrize functions in the generated class
 * @property returnParameter a type for casting the results (all found entities) to
 * @property parameters class parameters (from its constructor)
 * @property toListFunctionBody [CodeBlock] for toList() function
 * @property toSetFunctionBody [CodeBlock] for toSet() function
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
abstract class SelectorClassGenerator : ClassGenerator() {
    protected abstract val typeVariable: TypeVariableName
    protected abstract val returnParameter: TypeName
    protected abstract val parameters: List<ParameterSpec>
    protected open val toListFunctionBody = emptyListCode()
    protected open val toSetFunctionBody = statement("return toList().toSet()")

    /**
     * Specifies the builder to generate the class.
     */
    final override fun initBuilder() {
        super.initBuilder()
        builder.addTypeVariable(typeVariable)
        builder.primaryConstructor(FunSpec.constructorBuilder().addParameters(parameters).build())
        builder.addProperties(parameters.map {
            PropertySpec.builder(it.name, it.type).initializer(it.name).build()
        })
    }

    /**
     * Generates two functions in the class: toList() and toSet().
     */
    override fun generateImpl() = generateToListFunction()

    /**
     * Generates toList() function.
     */
    private fun generateToListFunction() = generateConversionFunction(List::class, toListFunctionBody)

    /**
     * A common function to generate toList() or toSet() functions.
     *
     * @param klass specifies the returned collection
     * @param body the function's body
     */
    private fun generateConversionFunction(
        klass: KClass<*>,
        body: CodeBlock,
    ) = addFunctions(
        generateFunction(
            name = "to${klass.simpleName}",
            body = body,
            returnType = klass.asClassName().parameterizedBy(returnParameter),
        ),
    )
}
