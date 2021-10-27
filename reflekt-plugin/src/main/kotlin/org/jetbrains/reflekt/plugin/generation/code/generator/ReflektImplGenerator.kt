package org.jetbrains.reflekt.plugin.generation.code.generator

import com.squareup.kotlinpoet.ClassName
import org.jetbrains.reflekt.plugin.analysis.models.ReflektUses
import org.jetbrains.reflekt.plugin.analysis.models.ir.flatten
import org.jetbrains.reflekt.plugin.generation.code.generator.models.*
import java.util.*

class ReflektImplGenerator(private val uses: ReflektUses) : FileGenerator() {
    override val packageName = "org.jetbrains.reflekt"
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
                FunctionsGenerator(typeName, uses.functions.flatten(), this@ReflektImplGenerator)
            )

            addFunctions(innerGenerators.map { generator ->
                generateFunction(
                    name = generator.typeName.simpleName.replaceFirstChar { it.lowercase(Locale.getDefault()) },
                    body = statement("return %T()", generator.typeName)
                )
            })
            addNestedTypes(innerGenerators.map { it.generate() })
        }
    }
}
