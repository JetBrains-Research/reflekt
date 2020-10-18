package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.generator.addSuffix
import io.reflekt.plugin.generator.notImplementedError
import io.reflekt.plugin.generator.statement
import io.reflekt.plugin.generator.wrappedCode
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
                    generateWhenBody(it, FQ_NAMES)
                } ?: notImplementedError()
            }
        }.generate())

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = run {
                // Delete items which don't have annotations
                generateNestedWhenBody(uses.filter { it.key.isNotEmpty() } as ClassOrObjectUses, ANNOTATION_FQ_NAMES, SUBTYPE_FQ_NAMES)
            }
        }.generate())
    }

    private fun listOfWhenRightPart(uses: List<String>) = statement(" listOf(${uses.joinToString(separator = ", ") { "${addSuffix(it, typeSuffix)} as %T" }})", *(MutableList(uses.size) { returnParameter }).toTypedArray())

    /*
    * Get something like this: setOf("invokes[0]", "invokes[1]" ...) -> listOf({uses[0] with typeSuffix} as %T, {uses[1] with typeSuffix} as %T)
    * */
    private fun getWhenOption(invokes: Set<String>, rightPart: CodeBlock): CodeBlock {
        return CodeBlock.builder()
            .add("setOf(${invokes.joinToString(separator = ", ") { "\"$it\"" }}) ->")
            .add(rightPart)
            .build()
    }

    private fun <T> generateWhenBody(uses: Iterable<T>, conditionVariable: String, mainFunction: (T) -> CodeBlock, toAddReturn: Boolean = true): CodeBlock {
        val builder = CodeBlock.builder()
        if (toAddReturn) {
            builder.add("return ")
        }
        builder.beginControlFlow("when (%N)", conditionVariable)
        uses.forEach{
            builder.add(mainFunction(it))
        }
        builder.addStatement("else -> error(%S)", UNKNOWN_FQ_NAME)
        builder.endControlFlow()
        return builder.build()
    }

    private fun generateWhenBody(uses: Map<Set<String>, List<String>>, conditionVariable: String, toAddReturn: Boolean = true): CodeBlock {
        val mainFunction = { (k, v): Map.Entry<Set<String>, List<String>> -> getWhenOption(k, listOfWhenRightPart(v)) }
        return generateWhenBody(uses.asIterable(), conditionVariable, mainFunction, toAddReturn)
    }

    private fun generateNestedWhenBody(uses: ClassOrObjectUses, annotationFqNames: String, subtypeFqNames: String): CodeBlock {
        val mainFunction = { o: Map.Entry<Set<String>, Map<Set<String>, List<String>>> ->
            getWhenOption(o.key, wrappedCode(generateWhenBody(o.value, subtypeFqNames, false)))
        }
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
