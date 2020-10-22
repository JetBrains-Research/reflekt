package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.reflekt.plugin.generator.generateFunction
import io.reflekt.plugin.generator.notImplementedError
import io.reflekt.plugin.generator.statement
import kotlin.reflect.KClass

abstract class SelectorClassGenerator : ClassGenerator() {
    protected abstract val typeVariable: TypeVariableName
    protected abstract val returnParameter: TypeName
    protected abstract val parameters: List<ParameterSpec>

    protected open val toListFunctionBody = notImplementedError()
    protected open val toSetFunctionBody = statement("return toList().toSet()")

    final override fun initBuilder() {
        super.initBuilder()
        builder.addTypeVariable(typeVariable)
        builder.primaryConstructor(FunSpec.constructorBuilder().addParameters(parameters).build())
        builder.addProperties(parameters.map {
            PropertySpec.builder(it.name, it.type).initializer(it.name).build()
        })
    }

    override fun generateImpl() {
        generateToListFunction()
        generateToSetFunction()
    }

    private fun generateToListFunction() = generateConversionFunction(List::class, toListFunctionBody)

    private fun generateToSetFunction() = generateConversionFunction(Set::class, toSetFunctionBody)

    private fun generateConversionFunction(
        klass: KClass<*>,
        body: CodeBlock
    ) =
        addFunctions(generateFunction(
            name = "to${klass.simpleName}",
            body = body,
            returnType = klass.asClassName().parameterizedBy(returnParameter)
        ))
}
