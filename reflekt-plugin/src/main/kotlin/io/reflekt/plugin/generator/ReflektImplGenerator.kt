package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.ClassName
import io.reflekt.plugin.generator.models.*

class ReflektImplGenerator : FileGenerator() {
    override val packageName = "io.reflekt"
    override val fileName = "ReflektImpl"

    override fun generateImpl() {
        addType(ReflektImplClassGenerator(packageName, fileName).generate())
    }

    private class ReflektImplClassGenerator(
        packageName: String,
        fileName: String
    ) : ObjectGenerator() {
        override val typeName = ClassName(packageName, fileName)

        override fun generateImpl() {
            val innerGenerators = listOf(
                ObjectsGenerator(typeName),
                ClassesGenerator(typeName),
                FunctionsGenerator(typeName)
            )

            addFunctions(innerGenerators.map {
                generateFunction(
                    name = it.typeName.simpleName.decapitalize(),
                    body = singleLineCode("return %T()", it.typeName)
                )
            })
            addNestedTypes(innerGenerators.map { it.generate() })
        }
    }
}
