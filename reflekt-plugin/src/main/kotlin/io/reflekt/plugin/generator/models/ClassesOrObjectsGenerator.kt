package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import io.reflekt.plugin.generator.singleLineCode
import kotlin.reflect.KClass

abstract class ClassesOrObjectsGenerator : HelperClassGenerator() {
    override fun generateImpl() {
        generateWithSubTypesFunction()
        generateWithAnnotationsFunction()

        addNestedType(object : WithSubTypesGenerator() {
            override val toListFunctionBody = singleLineCode("error(%S)", "Not implemented")
        }.generate())

        addNestedType(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = singleLineCode("error(%S)", "Not implemented")
        }.generate())
    }
}

class ClassesGenerator(enclosingClassName: ClassName) : ClassesOrObjectsGenerator() {
    override val typeName: ClassName = enclosingClassName.nestedClass("Classes")
    override val typeVariable = TypeVariableName("T", Any::class)
    override val returnParameter = KClass::class.asClassName().parameterizedBy(typeVariable)
}

class ObjectsGenerator(enclosingClassName: ClassName) : ClassesOrObjectsGenerator() {
    override val typeName: ClassName = enclosingClassName.nestedClass("Objects")
    override val typeVariable = TypeVariableName("T")
    override val returnParameter = typeVariable
}
