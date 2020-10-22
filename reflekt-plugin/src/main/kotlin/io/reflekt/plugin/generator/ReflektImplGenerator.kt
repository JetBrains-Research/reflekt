package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.ClassName
import io.reflekt.plugin.analysis.ReflektUses
import io.reflekt.plugin.generator.models.*

class ReflektImplGenerator(private val uses: ReflektUses) : FileGenerator() {
    override val packageName = "io.reflekt"
    override val fileName = "ReflektImpl"

    override fun generateImpl() {
        addTypes(ReflektImplClassGenerator().generate())
    }

    private inner class ReflektImplClassGenerator : ObjectGenerator() {
        override val typeName = ClassName(packageName, fileName)

        override fun generateImpl() {
            val innerGenerators = listOf(
                ObjectsGenerator(typeName, uses.objects),
                ClassesGenerator(typeName, uses.classes),
                FunctionsGenerator(typeName, uses.functions, this@ReflektImplGenerator)
            )

            addFunctions(innerGenerators.map {
                generateFunction(
                    name = it.typeName.simpleName.decapitalize(),
                    body = statement("return %T()", it.typeName)
                )
            })
            addNestedTypes(innerGenerators.map { it.generate() })
        }
    }
}
