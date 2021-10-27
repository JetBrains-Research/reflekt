package org.jetbrains.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.reflekt.plugin.analysis.models.ClassOrObjectUses
import org.jetbrains.reflekt.plugin.analysis.models.ir.toSupertypesToFqNamesMap
import org.jetbrains.reflekt.plugin.generation.code.generator.emptyListCode
import kotlin.reflect.KClass

abstract class ClassesOrObjectsGenerator(protected val uses: ClassOrObjectUses) : HelperClassGenerator() {
    override fun generateImpl() {
        generateWithSupertypesFunction()
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithSupertypesGenerator() {
            override val toListFunctionBody = run {
                // Get item without annotations
                val supertypesToFqNames = HashMap(uses.filter { it.key.annotations.isEmpty() }).toSupertypesToFqNamesMap()
                if (supertypesToFqNames.isNotEmpty()) {
                    generateWhenBody(supertypesToFqNames, FQ_NAMES, getWhenOption = ::getWhenOptionForSet)
                } else {
                    emptyListCode()
                }
            }
        }.generate())

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = run {
                // Delete items which don't have annotations
                generateNestedWhenBodyForClassesOrObjects(HashMap(uses.filter { it.key.annotations.isNotEmpty() }))
            }
        }.generate())
    }
}

class ClassesGenerator(enclosingClassName: ClassName, uses: ClassOrObjectUses) : ClassesOrObjectsGenerator(uses) {
    override val typeName: ClassName = enclosingClassName.nestedClass("Classes")
    override val typeVariable = TypeVariableName("T", Any::class)
    override val returnParameter = KClass::class.asClassName().parameterizedBy(typeVariable)
    override val typeSuffix = "::class"
}

class ObjectsGenerator(enclosingClassName: ClassName, uses: ClassOrObjectUses) : ClassesOrObjectsGenerator(uses) {
    override val typeName: ClassName = enclosingClassName.nestedClass("Objects")
    override val typeVariable = TypeVariableName("T")
    override val returnParameter = typeVariable
}
