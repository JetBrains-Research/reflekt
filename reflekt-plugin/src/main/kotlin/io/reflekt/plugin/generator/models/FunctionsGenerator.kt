package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import io.reflekt.plugin.generator.singleLineCode
import kotlin.reflect.KFunction

class FunctionsGenerator(enclosingClassName: ClassName) : HelperClassGenerator() {
    override val typeName: ClassName = enclosingClassName.nestedClass("Functions")
    override val typeVariable = TypeVariableName("T", Any::class)
    override val returnParameter = KFunction::class.asClassName().parameterizedBy(typeVariable)

    override fun generateImpl() {
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithAnnotationsGenerator() {
            // TODO implement toList()
        }.generate())
    }
}
