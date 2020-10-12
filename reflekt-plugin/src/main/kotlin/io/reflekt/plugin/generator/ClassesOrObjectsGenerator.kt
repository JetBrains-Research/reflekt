package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import kotlin.reflect.KClass

abstract class ClassesOrObjectsGenerator : BaseHelperClassGenerator() {

    override fun generateImpl() {
        generateWithSubTypesFunction()
        generateWithAnnotationsFunction()

        addNestedType(object : WithSubTypesGenerator() {
            override val toSetFunctionBody = singleLineCode("error(%S)", "Not implemented")

            override fun generateImpl() {
                generateToListFunction()
                generateToSetFunction()
            }
        }.generate())

        addNestedType(object : WithAnnotationsGenerator() {
            override val toSetFunctionBody = singleLineCode("error(%S)", "Not implemented")

            override fun generateImpl() {
                generateToListFunction()
                generateToSetFunction()
            }
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
