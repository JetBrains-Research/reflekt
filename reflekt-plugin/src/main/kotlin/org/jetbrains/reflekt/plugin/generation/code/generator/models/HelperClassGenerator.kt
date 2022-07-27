@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.reflekt.plugin.analysis.models.SignatureToAnnotations
import org.jetbrains.reflekt.plugin.analysis.models.SupertypesToAnnotations
import org.jetbrains.reflekt.plugin.analysis.models.ir.ClassOrObjectLibraryQueriesResults
import org.jetbrains.reflekt.plugin.analysis.models.ir.FunctionLibraryQueriesResults
import org.jetbrains.reflekt.plugin.generation.code.generator.*
import org.jetbrains.reflekt.plugin.utils.stringRepresentation
import java.util.*

/**
 * An abstract class for common functionality for top-level classes in the DSL,
 *  e.g. class Objects, class Classes, and class Functions.
 *
 *  @property typeVariable a generic variable to parametrize functions in the generated class
 *  @property returnParameter a type for casting the results (all found entities) to
 *  @property withSupertypesFunctionBody the body of the withSupertypes function
 *  @property withAnnotationsFunctionBody the body of the withAnnotations function
 *  @property withSupertypesParameters parameters for the WithSuperTypes class (from its constructor)
 *  @property withAnnotationsParameters parameters for the WithAnnotations class (from its constructor)
 *
 *  TODO: can we separate different functions into different classes,
 *   since we don't need all of them in each generated classes,
 *   e.g for functions we should generate only withAnnotations function.
 *   Also currently we have different variable for one entity
 *   (e.g. withSupertypesFunctionBody, withSupertypesParameters, generateWithSupertypesFunction)
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY", "UnnecessaryAbstractClass")
abstract class HelperClassGenerator : ClassGenerator() {
    abstract val typeVariable: TypeVariableName
    abstract val returnParameter: TypeName

    open val withSupertypesFunctionBody: CodeBlock
        get() = statement(
            "return %T(%N)",
            typeName.nestedClass(WITH_SUPERTYPES_CLASS_NAME).parameterizedBy(typeVariable),
            FQ_NAMES,
        )

    open val withAnnotationsFunctionBody: CodeBlock
        get() = statement(
            "return %T(%N, %N)",
            typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME).parameterizedBy(typeVariable),
            ANNOTATION_FQ_NAMES,
            SUPERTYPE_FQ_NAMES,
        )

    open val withSupertypesParameters = mapOf(
        FQ_NAMES to SET_OF_STRINGS,
    ).toParameterSpecs()
    open val withAnnotationsParameters = mapOf(
        ANNOTATION_FQ_NAMES to SET_OF_STRINGS,
        SUPERTYPE_FQ_NAMES to SET_OF_STRINGS,
    ).toParameterSpecs()

    /**
     * Generates withSupertypes function from the ReflektImpl.kt file.
     */
    fun generateWithSupertypesFunction() {
        builder.addFunction(
            generateFunction(
                name = WITH_SUPERTYPES_FUNCTION_NAME,
                body = withSupertypesFunctionBody,
                typeVariables = listOf(typeVariable),
                arguments = withSupertypesParameters,
            ),
        )
    }

    /**
     * Generates withAnnotations function from the ReflektImpl.kt file.
     */
    fun generateWithAnnotationsFunction() {
        builder.addFunction(
            generateFunction(
                name = WITH_ANNOTATIONS_FUNCTION_NAME,
                body = withAnnotationsFunctionBody,
                typeVariables = listOf(typeVariable),
                arguments = withAnnotationsParameters,
            ),
        )
    }

    /**
     * Generates a list of entities for the right part of the 'when' operator.
     *
     * @param uses found entities
     * @param getEntityName gets name of the entity from the uses
     * @return generated [CodeBlock]:
     *  listOf({uses[0] with typeSuffix} as %T, {uses[1] with typeSuffix} as %T)
     */
    @Suppress("SpreadOperator")
    protected open fun <T> listOfWhenRightPart(uses: List<T>, getEntityName: (T) -> String) =
        statement(
            "listOf(${uses.joinToString(separator = ", ") { "${getEntityName(it)} as %T" }})",
            *List(uses.size) { returnParameter }.toTypedArray(),
        )

    /**
     * Generates 'when' option for a set of entities as a left part.
     *
     *  @param invokes set of invokes (arguments from the Reflekt query,
     *   e.g. fully qualified names of the annotations that should be found)
     *  @param rightPart [CodeBlock] of already generated right part of this 'when' option
     *  @return generated [CodeBlock]:
     *   setOf("invokes[0]", "invokes[1]" ...) -> listOf({uses[0] with typeSuffix} as %T, {uses[1] with typeSuffix} as %T)
     *   the right part of this option should be generated earlier and passed into this function as [rightPart]
     */
    fun getWhenOptionForSet(invokes: Set<String>, rightPart: CodeBlock): CodeBlock {
        val setOfBlock = if (invokes.isEmpty()) {
            "emptySet<String>()"
        } else {
            "setOf(${invokes.joinToString(separator = ", ") { "\"$it\"" }})"
        }
        return getWhenOption(setOfBlock, rightPart)
    }

    /**
     * Generates 'when' option for a string as a left part.
     *
     * @param invoke an invoke string representation (an argument from the Reflekt query,
     *   e.g. fully qualified name of the annotation that should be found)
     * @param rightPart [CodeBlock] of already generated right part of this 'when' option
     * @return generated [CodeBlock]:
     *  [invoke] -> listOf({uses[0] with typeSuffix} as %T, {uses[1] with typeSuffix} as %T)
     *  the right part of this option should be generated earlier and passed into this function as [rightPart]
     */
    private fun getWhenOptionForString(invoke: String, rightPart: CodeBlock) = getWhenOption("\"$invoke\"", rightPart)

    /**
     * Generates 'when' option by adding [leftPart] and [rightPart] parts.
     *
     * @param leftPart [String] for a left part of the 'when' option
     * @param rightPart [CodeBlock] for a right part of the 'when' option
     * @return generated [CodeBlock]: [leftPart] -> [rightPart]
     */
    private fun getWhenOption(leftPart: String, rightPart: CodeBlock) = buildCodeBlock {
        add(leftPart)
        add(" -> ")
        add(rightPart)
    }

    /**
     * An internal function to generate the full 'when' body.
     *
     * @param invokesWithUses collection of invokes (arguments from the Reflekt queries)
     *  and uses (found entities) that satisfy these invokes
     * @param conditionVariable the 'when' condition variable
     * @param toAddReturn if {@code true}, adds the {@code return} statement before the 'when' operator
     * @param generateBranchForWhenOption a function to generate one branch for one 'when' option
     * @return generated [CodeBlock]:
     *   <optional return> when ([conditionVariable]) {
     *       ...
     *       branches, that generated by [generateBranchForWhenOption] for each item from [invokesWithUses]
     *       ...
     *       else -> emptyList()
     *   }
     */
    private fun <T> generateWhenBody(
        invokesWithUses: Iterable<T>,
        conditionVariable: String,
        toAddReturn: Boolean = true,
        generateBranchForWhenOption: (T) -> CodeBlock,
    ): CodeBlock = buildCodeBlock {
        if (toAddReturn) {
            add("return ")
        }
        beginControlFlow("when (%N)", conditionVariable)
        for (invoke in invokesWithUses) {
            add(generateBranchForWhenOption(invoke))
        }
        addStatement("else -> emptyList()")
        endControlFlow()
    }

    /**
     * Generates the full 'when' body.
     *
     * @param invokesWithUses collection of invokes (arguments from the Reflekt queries)
     *  and uses (found entities) that satisfy these invokes
     * @param conditionVariable the 'when' condition variable
     * @param getEntityName gets name of the entity from the uses
     * @param toAddReturn if {@code true}, adds the {@code return} statement before the 'when' operator
     * @param getWhenOption a function to generate one 'when' option, e.g. [getWhenOptionForString] or [getWhenOptionForSet]
     *  To make from this function a function for the [generateWhenBody] arguments, it is necessary to
     *  apply any function to uses for each invokes (from [invokesWithUses]), e.g. [listOfWhenRightPart]
     * @return generated [CodeBlock]:
     *   <optional return> when ([conditionVariable]) {
     *       setOf(invokes) -> listOf(uses that satisfy the invokes)
     *       ...
     *       else -> emptyList()
     *   }
     */
    @Suppress(
        "LAMBDA_IS_NOT_LAST_PARAMETER",
        "IDENTIFIER_LENGTH",
        "TYPE_ALIAS",
    )
    protected fun <K, T> generateWhenBody(
        invokesWithUses: Map<K, List<T>>,
        conditionVariable: String,
        getEntityName: (T) -> String = { it.toString() },
        toAddReturn: Boolean = true,
        getWhenOption: (K, CodeBlock) -> CodeBlock,
    ): CodeBlock {
        val generateBranchForWhenOption = { (k, v): Map.Entry<K, List<T>> -> getWhenOption(k, listOfWhenRightPart(v, getEntityName)) }
        return generateWhenBody(invokesWithUses.asIterable(), conditionVariable, toAddReturn, generateBranchForWhenOption)
    }

    /**
     * Generate the full 'when' with a nested one for functions.
     *
     * @param invokesWithUses collection of invokes (arguments from the Reflekt queries)
     *  and uses (found entities) that satisfy these invokes.
     *  In this case invokes are [SignatureToAnnotations]
     * @param getEntityName a function that gets a string representation for [IrFunction]
     * @return generated [CodeBlock]:
     *  when ([ANNOTATION_FQ_NAMES]) {
     *   ...
     *   setOf(annotations from [invokesWithUses]) -> {
     *       when ([SIGNATURE]) -> {
     *           ...
     *           signature from [invokesWithUses] -> listOf([getEntityName] as KFunction<T>, ...)
     *           ...
     *           else -> emptyList()
     *       }
     *    ...
     *    else -> emptyList()
     *  }
     */
    @Suppress("TYPE_ALIAS", "IDENTIFIER_LENGTH")
    // TODO: group by annotations (store set of signatures for the same set of annotations)
    protected fun generateNestedWhenBodyForFunctions(
        invokesWithUses: FunctionLibraryQueriesResults,
        getEntityName: (IrFunction) -> String = { it.toString() },
    ): CodeBlock {
        val mainFunction = { o: Map.Entry<SignatureToAnnotations, List<String>> ->
            getWhenOptionForSet(
                o.key.annotations,
                wrappedCode(
                    generateWhenBody(
                        mapOf(o.key.irSignature!!.stringRepresentation() to o.value),
                        SIGNATURE,
                        toAddReturn = false,
                        getWhenOption = ::getWhenOptionForString,
                    ),
                ),
            )
        }
        return generateWhenBody(
            invokesWithUses.mapValues { (_, v) -> v.map { getEntityName(it) } }.toMap().asIterable(),
            ANNOTATION_FQ_NAMES,
            generateBranchForWhenOption = mainFunction,
        )
    }

    /**
     * Generates the full 'when' with a nested one for classes and objects.
     *
     * @param invokesWithUses collection of invokes (arguments from the Reflekt queries)
     *  and uses (found entities) that satisfy these invokes.
     *  In this case invokes are [SignatureToAnnotations]
     * @return generated [CodeBlock]:
     *  when ([ANNOTATION_FQ_NAMES]) {
     *   ...
     *   setOf(annotations from [invokesWithUses]) -> {
     *       when ([SUPERTYPE_FQ_NAMES]) -> {
     *           ...
     *           setOf(signatures from [invokesWithUses]) -> listOf(<a found entity>::class as KClass<T>, ...) for classes
     *           OR
     *           setOf(signatures from [invokesWithUses]) -> listOf(<a found entity> as T, ...) for objects
     *           ...
     *           else -> emptyList()
     *       }
     *    ...
     *    else -> emptyList()
     *  }
     */
    @Suppress("TYPE_ALIAS", "IDENTIFIER_LENGTH")
    protected fun generateNestedWhenBodyForClassesOrObjects(invokesWithUses: ClassOrObjectLibraryQueriesResults): CodeBlock {
        val mainFunction = { o: Map.Entry<SupertypesToAnnotations, List<String>> ->
            getWhenOptionForSet(
                o.key.annotations,
                wrappedCode(
                    generateWhenBody(
                        mapOf(o.key.supertypes to o.value),
                        SUPERTYPE_FQ_NAMES,
                        toAddReturn = false,
                        getWhenOption = ::getWhenOptionForSet,
                    ),
                ),
            )
        }
        return generateWhenBody(
            invokesWithUses.mapValues { (_, v) -> v.mapNotNull { it.fqNameWhenAvailable?.toString() } }.toMap().asIterable(),
            ANNOTATION_FQ_NAMES,
            generateBranchForWhenOption = mainFunction,
        )
    }

    /**
     * A wrapper to specify selector classes, e.g. WithSuperTypes or WithAnnotations.
     *
     * @property typeName a fully-qualified class name
     * @property typeVariable a generic variable to parametrize functions in the generated class
     * @property parameters class parameters (from its constructor)
     * @property returnParameter a type for casting the results (all found entities) to
     */
    protected abstract class SelectorClassGeneratorWrapper(
        override val typeName: ClassName,
        override val typeVariable: TypeVariableName,
        override val parameters: List<ParameterSpec>,
        override val returnParameter: TypeName,
    ) : SelectorClassGenerator()

    protected companion object {
        const val ANNOTATION_FQ_NAMES = "annotationFqNames"
        const val FQ_NAMES = "fqNames"
        const val SIGNATURE = "signature"
        const val SUPERTYPE_FQ_NAMES = "supertypeFqNames"
        const val WITH_ANNOTATIONS_FUNCTION_NAME = "withAnnotations"
        const val WITH_SUPERTYPES_FUNCTION_NAME = "withSuperTypes"
        val WITH_SUPERTYPES_CLASS_NAME =
            WITH_SUPERTYPES_FUNCTION_NAME.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val WITH_ANNOTATIONS_CLASS_NAME =
            WITH_ANNOTATIONS_FUNCTION_NAME.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val SET_OF_STRINGS = Set::class.parameterizedBy(String::class)
    }

    /**
     * Generator for the WithAnnotations class.
     *
     * @property typeName a fully-qualified class name
     * @property typeVariable a generic variable to parametrize used functions in the generated class
     * @property parameters class parameters (from its constructor)
     * @property returnParameter a type for casting the results (all found entities) to
     */
    protected abstract inner class WithSupertypesGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_SUPERTYPES_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withSupertypesParameters,
        returnParameter = this.returnParameter,
    )

    /**
     * Generator for the WithSuperTypes class
     *
     * @property typeName a fully-qualified class name
     * @property typeVariable a generic variable to parametrize functions in the generated class
     * @property parameters class parameters (from its constructor)
     * @property returnParameter a type for casting the results (all found entities)
     */
    protected abstract inner class WithAnnotationsGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withAnnotationsParameters,
        returnParameter = this.returnParameter,
    )
}
