package io.reflekt.plugin.generation.code.generator.models

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.generation.code.generator.statement
import io.reflekt.plugin.generation.code.generator.toParameterSpecs

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.psi.KtNamedFunction

import kotlin.reflect.KFunction

class FunctionsGenerator(
    enclosingClassName: ClassName,
    private val uses: FunctionUses,
    private val fileGenerator: FileGenerator) : HelperClassGenerator() {
    override val typeName: ClassName = enclosingClassName.nestedClass("Functions")
    override val typeVariable = TypeVariableName("T", Any::class)
    override val returnParameter = KFunction::class.asClassName().parameterizedBy(typeVariable)

    override val withAnnotationsFunctionBody: CodeBlock
        get() = statement(
            "return %T(%N, %N)",
            typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME).parameterizedBy(typeVariable),
            ANNOTATION_FQ_NAMES,
            SIGNATURE,
        )

    override val withAnnotationsParameters = mapOf(
        ANNOTATION_FQ_NAMES to SET_OF_STRINGS,
        SIGNATURE to String::class.asClassName(),
    ).toParameterSpecs()

    override fun generateImpl() {
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = run {
                generateNestedWhenBodyForFunctions(uses, ::functionReference)
            }
        }.generate())
    }

    private fun functionReference(function: KtNamedFunction): String =
            if (function.isTopLevel) {
                val packageName = function.fqName!!.parent().toString()
                val name = function.name!!
                val memberName = MemberName(packageName, name)
                "::${fileGenerator.addUniqueAliasedImport(memberName)}"
            } else {
                "${function.fqName!!.parent()}::${function.name}"
            }
}
