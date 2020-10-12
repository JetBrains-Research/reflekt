package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import kotlin.reflect.KFunction

class FunctionsGenerator(enclosingClassName: ClassName) : BaseHelperClassGenerator() {
    override val typeName: ClassName = enclosingClassName.nestedClass("Functions")
    override val typeVariable = TypeVariableName("T", Any::class)
    override val returnParameter = KFunction::class.asClassName().parameterizedBy(typeVariable)

    override fun generateImpl() {
        generateWithAnnotationsFunction()

        addNestedType(object : WithAnnotationsGenerator() {
            override val toSetFunctionBody = singleLineCode("error(%S)", "Not implemented")

            override fun generateImpl() {
                generateToListFunction()
                generateToSetFunction()
            }
        }.generate())
    }
}
