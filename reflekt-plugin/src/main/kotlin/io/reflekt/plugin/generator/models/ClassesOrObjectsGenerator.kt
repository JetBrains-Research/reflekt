package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import kotlin.reflect.KClass

abstract class ClassesOrObjectsGenerator : HelperClassGenerator() {
    override fun generateImpl() {
        generateWithSubTypesFunction()
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithSubTypesGenerator() {
            // TODO implement toList()
        }.generate())

        addNestedTypes(object : WithAnnotationsGenerator() {
            // TODO implement toList()
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
