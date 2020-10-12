package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

abstract class BaseSelectorClassGenerator : ClassGenerator() {
    protected abstract val typeVariable: TypeVariableName
    protected abstract val returnParameter: TypeName
    protected abstract val parameters: List<ParameterSpec>

    protected open val toListFunctionBody
        = singleLineCode("return toSet().toList()")
    protected open val toSetFunctionBody
        = singleLineCode("return toList().toSet()")

    override fun initBuilder() {
        super.initBuilder()
        builder.addTypeVariable(typeVariable)
        builder.primaryConstructor(FunSpec.constructorBuilder().addParameters(parameters).build())
        builder.addProperties(parameters.map {
            PropertySpec.builder(it.name, it.type).initializer(it.name).build()
        })
    }

    fun generateToListFunction() {
        generateConversionFunction(List::class, toListFunctionBody)
    }

    fun generateToSetFunction() {
        generateConversionFunction(Set::class, toSetFunctionBody)
    }

    private fun generateConversionFunction(
        klass: KClass<*>,
        body: CodeBlock
    ) {
        addFunction(generateFunction(
            name = "to${klass.simpleName}",
            body = body,
            returnType = klass.asClassName().parameterizedBy(returnParameter)
        ))
    }
}
