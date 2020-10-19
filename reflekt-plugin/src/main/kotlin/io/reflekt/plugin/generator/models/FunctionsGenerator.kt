package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.reflekt.plugin.analysis.FunctionUses
import io.reflekt.plugin.generator.statement
import kotlin.reflect.KFunction

class FunctionsGenerator(enclosingClassName: ClassName, private val uses: FunctionUses) : HelperClassGenerator() {
    override val typeName: ClassName = enclosingClassName.nestedClass("Functions")
    override val typeVariable = TypeVariableName("T", Any::class)
    override val returnParameter = KFunction::class.asClassName().parameterizedBy(typeVariable)

    override fun listOfWhenRightPart(uses: List<String>) = statement(" listOf(${List(uses.size) { "::%M as %T" }.joinToString(separator = ", ")})",
        // FIXME it's not ok
        *Array<Any>(uses.size * 2) {
            i -> if (i % 2 == 0) MemberName(uses[i / 2].substringBeforeLast('.'), uses[i / 2].substringAfterLast('.')) else returnParameter
        })

    override fun generateImpl() {
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = generateWhenBody(uses, ANNOTATION_FQ_NAMES)
        }.generate())
    }
}
