package io.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.toSubTypesToFqNamesMap
import io.reflekt.plugin.generation.code.generator.emptyListCode
import kotlin.reflect.KClass

abstract class ClassesOrObjectsGenerator(protected val uses: ClassOrObjectUses) : HelperClassGenerator() {
    override fun generateImpl() {
        generateWithSubTypesFunction()
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithSubTypesGenerator() {
            override val toListFunctionBody = run {
                // Get item without annotations
                val subTypesToFqNames = uses.filter { it.key.annotations.isEmpty() }.toSubTypesToFqNamesMap()
                if (subTypesToFqNames.isNotEmpty()) {
                    generateWhenBody(subTypesToFqNames, FQ_NAMES)
                } else {
                    emptyListCode()
                }
            }
        }.generate())

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = run {
                // Delete items which don't have annotations
                generateNestedWhenBody(uses.filter { it.key.annotations.isNotEmpty() }, ANNOTATION_FQ_NAMES, SUBTYPE_FQ_NAMES)
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
