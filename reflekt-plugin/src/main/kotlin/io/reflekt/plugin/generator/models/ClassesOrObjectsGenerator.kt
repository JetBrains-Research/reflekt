package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.generator.addSuffix
import io.reflekt.plugin.generator.models.FileGenerator.Companion.indent
import io.reflekt.plugin.generator.notImplementedError
import io.reflekt.plugin.generator.singleLineCode
import kotlin.reflect.KClass

abstract class ClassesOrObjectsGenerator(protected val uses: ClassOrObjectUses) : HelperClassGenerator() {
    protected open val typeSuffix: String = ""

    override fun generateImpl() {
        generateWithSubTypesFunction()
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithSubTypesGenerator() {
            override val toListFunctionBody = run {
                // Get item without annotations
                uses[emptySet()]?.let {
                    generateWhenBody(it, FQ_NAMES).build()
                } ?: notImplementedError()
            }
        }.generate())

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = run {
                // Delete items which don't have annotations
                generateNestedWhenBody(uses.filter { it.key.isNotEmpty() } as ClassOrObjectUses, ANNOTATION_FQ_NAMES, SUBTYPE_FQ_NAMES).build()
            }
        }.generate())
    }

    private fun listOfWhenRightPart(uses: List<String>) = singleLineCode("listOf(${uses.joinToString(separator = ", ") { "${addSuffix(it, typeSuffix)} as %T" }})", *(MutableList(uses.size) { returnParameter }).toTypedArray())

    /*
    * Get something like this: setOf("invokes[0]", "invokes[1]" ...) -> listOf({uses[0] with typeSuffix} as %T, {uses[1] with typeSuffix} as %T)
    * */
    private fun getWhenOption(invokes: Set<String>, rightPart: CodeBlock, ind: String = indent): String {
        return "${ind}setOf(${invokes.joinToString(separator = ", ") { "\"${it}\"" }}) -> $rightPart"
    }

    private fun <T> generateWhenBody(uses: Iterable<T>, conditionVariable: String, mainFunction: (T) -> String, toAddReturn: Boolean = true): CodeBlock.Builder {
        val builder = CodeBlock.builder()
        if (toAddReturn) {
            builder.add("return ")
        }
        builder.add("when(%N) {\n", conditionVariable)
        uses.forEach{
            // TODO: what should I do with indents?? Is it a normal way??
            builder.add(mainFunction(it))
        }
        builder.add("${indent}else -> error(%S)", UNKNOWN_FQ_NAME)
        builder.add("\n}")
        return builder
    }

    private fun generateWhenBody(uses: Map<Set<String>, List<String>>, conditionVariable: String, toAddReturn: Boolean = true): CodeBlock.Builder {
        val mainFunction = { (k, v): Map.Entry<Set<String>, List<String>> -> getWhenOption(k, listOfWhenRightPart(v)) }
        return generateWhenBody(uses.asIterable(), conditionVariable, mainFunction, toAddReturn)
    }

    private fun generateNestedWhenBody(uses: ClassOrObjectUses, annotationFqNames: String, subtypeFqNames: String): CodeBlock.Builder {
        val mainFunction = { o: Map.Entry<Set<String>, Map<Set<String>, List<String>>> -> getWhenOption(o.key, CodeBlock.builder().add("{\n${generateWhenBody(o.value, subtypeFqNames, false).build()}\n}\n").build()) }
        return generateWhenBody(uses.asIterable(), annotationFqNames, mainFunction)
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
