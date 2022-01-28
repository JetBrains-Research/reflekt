package org.jetbrains.reflekt.plugin.generation.code.generator.models

import org.jetbrains.reflekt.plugin.generation.code.generator.*

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

import kotlin.reflect.KClass

/**
 * An abstract class to generate a new internal class from the DSL, e.g. WithSuperTypes or WithAnnotations
 *
 * @property typeVariable a generic variable to parametrize used functions in the generated class
 * @property returnParameter a type for  casting the results (all found entities)
 * @property parameters class parameters (from its constructor)
 * @property [CodeBlock] for toList() function
 * @property [CodeBlock] for toSet() function
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
abstract class SelectorClassGenerator : ClassGenerator() {
    protected abstract val typeVariable: TypeVariableName
    protected abstract val returnParameter: TypeName
    protected abstract val parameters: List<ParameterSpec>
    protected open val toListFunctionBody = emptyListCode()
    protected open val toSetFunctionBody = statement("return toList().toSet()")

    /**
     * Specify the builder to generate the class
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
     * Generate two function in the class: toList() and toSet()
     */
    override fun generateImpl() {
        generateToListFunction()
        generateToSetFunction()
    }

    /**
     * Generate toList() function
     */
    private fun generateToListFunction() = generateConversionFunction(List::class, toListFunctionBody)

    /**
     * Generate toSet() function
     */
    private fun generateToSetFunction() = generateConversionFunction(Set::class, toSetFunctionBody)

    /**
     * The common function to generate toList() or toSet() function
     *
     * @param klass specify the returned collection
     * @param body the function's body
     */
    private fun generateConversionFunction(
        klass: KClass<*>,
        body: CodeBlock,
    ) =
        addFunctions(
            generateFunction(
                name = "to${klass.simpleName}",
                body = body,
                returnType = klass.asClassName().parameterizedBy(returnParameter),
            ),
        )
}
