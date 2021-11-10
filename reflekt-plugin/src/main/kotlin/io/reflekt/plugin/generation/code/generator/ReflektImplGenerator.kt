package io.reflekt.plugin.generation.code.generator

import io.reflekt.plugin.analysis.models.ReflektUses
import io.reflekt.plugin.analysis.models.flatten
import io.reflekt.plugin.generation.code.generator.models.*
import io.reflekt.plugin.generation.code.generator.models.ClassesGenerator
import io.reflekt.plugin.generation.code.generator.models.ObjectsGenerator

import com.squareup.kotlinpoet.ClassName

import java.util.*

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
                ObjectsGenerator(typeName, uses.objects.flatten()),
                ClassesGenerator(typeName, uses.classes.flatten()),
                FunctionsGenerator(typeName, uses.functions.flatten(), this@ReflektImplGenerator),
            )

            addFunctions(innerGenerators.map { generator ->
                generateFunction(
                    name = generator.typeName.simpleName.replaceFirstChar { it.lowercase(Locale.getDefault()) },
                    body = statement("return %T()", generator.typeName),
                )
            })
            addNestedTypes(innerGenerators.map { it.generate() })
        }
    }
}
