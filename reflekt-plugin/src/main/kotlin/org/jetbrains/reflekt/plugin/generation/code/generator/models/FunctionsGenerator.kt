@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.isTopLevel
import org.jetbrains.reflekt.plugin.analysis.models.ir.FunctionLibraryQueriesResults
import org.jetbrains.reflekt.plugin.generation.code.generator.statement
import org.jetbrains.reflekt.plugin.generation.code.generator.toParameterSpecs
import kotlin.reflect.KFunction

/**
 * Generates a top level class Functions in the ReflektImpl.kt.
 *
 * @param enclosingClassName
 * @param libraryQueriesResults stores entities that satisfy all Reflekt queries arguments (invokes)
 * @param fileGenerator a generator that can generate new unique aliased imports for functions
 * @property typeName a fully-qualified class name
 * @property typeVariable a generic variable to parametrize functions in the generated class
 * @property returnParameter a type for casting the results (all found entities) to
 * @property withAnnotationsFunctionBody the body of the withAnnotations function
 * @property withAnnotationsParameters parameters for the WithAnnotations class (from its constructor)
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
class FunctionsGenerator(
    enclosingClassName: ClassName,
    private val libraryQueriesResults: FunctionLibraryQueriesResults,
    private val fileGenerator: FileGenerator,
) : HelperClassGenerator() {
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
     * The main function to generate the Functions class in the ReflektImpl.kt.
     */
    override fun generateImpl() {
        generateWithAnnotationsFunction()

        addNestedTypes(object : WithAnnotationsGenerator() {
            override val toListFunctionBody = run {
                generateNestedWhenBodyForFunctions(libraryQueriesResults, ::functionReference)
            }
        }.generate())
    }

    /**
     * Generates a function reference for the generated file.
     *
     * @param function
     * @return a string representation of the function reference
     */
    private fun functionReference(function: IrFunction): String =
        if (function.isTopLevel) {
            val packageName = function.fqNameWhenAvailable!!.parent().toString()
            val name = function.name.toString()
            val memberName = MemberName(packageName, name)
            "::${fileGenerator.addUniqueAliasedImport(memberName)}"
        } else {
            "${function.fqNameWhenAvailable!!.parent()}::${function.name}"
        }
}
