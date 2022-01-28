package org.jetbrains.reflekt.plugin.generation.code.generator.models

import org.jetbrains.reflekt.plugin.analysis.models.psi.FunctionUses
import org.jetbrains.reflekt.plugin.generation.code.generator.statement
import org.jetbrains.reflekt.plugin.generation.code.generator.toParameterSpecs

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.psi.KtNamedFunction

import kotlin.reflect.KFunction

/**
 * Class to generate a top level class Functions in the ReflektImpl.kt
 *
 * @param enclosingClassName
 * @param uses stores entities that satisfy all Reflekt queries arguments (invokes)
 * @param fileGenerator the generator that can generate new unique aliased imports for functions
 *
 * @property typeName a fully-qualified class name
 * @property typeVariable a generic variable to parametrize used functions in the generated class
 * @property returnParameter a type for casting the results (all found entities)
 * @property withAnnotationsFunctionBody the body of the withAnnotations function
 * @property withAnnotationsParameters parameters for the WithAnnotations class (from its constructor)
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
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

    /**
     * The main function to generate the Functions class in the ReflektImpl.kt
     */
    override fun generateImpl() {
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = run {
                generateNestedWhenBodyForFunctions(uses, ::functionReference)
            }
        }.generate())
    }

    /**
     * Generate a function reference for the generated file
     *
     * @param function
     * @return a string representation of the function reference
     */
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
