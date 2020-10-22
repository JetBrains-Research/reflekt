package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.reflekt.plugin.analysis.FunctionUses
import org.jetbrains.kotlin.psi.KtNamedFunction
import kotlin.reflect.KFunction

class FunctionsGenerator(enclosingClassName: ClassName, private val uses: FunctionUses, private val fileGenerator: FileGenerator) : HelperClassGenerator() {
    override val typeName: ClassName = enclosingClassName.nestedClass("Functions")
    override val typeVariable = TypeVariableName("T", Any::class)
    override val returnParameter = KFunction::class.asClassName().parameterizedBy(typeVariable)

    override fun generateImpl() {
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = generateWhenBody(uses, ANNOTATION_FQ_NAMES, ::functionReference)
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
