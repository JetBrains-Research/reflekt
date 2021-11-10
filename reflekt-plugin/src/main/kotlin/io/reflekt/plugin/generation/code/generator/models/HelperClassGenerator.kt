package io.reflekt.plugin.generation.code.generator.models

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.generation.code.generator.*
import io.reflekt.plugin.utils.stringRepresentation

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.psi.KtNamedFunction

import java.util.*

abstract class HelperClassGenerator : ClassGenerator() {
    abstract val typeVariable: TypeVariableName
    abstract val returnParameter: TypeName
    protected open val typeSuffix: String = ""

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

    private fun <T> listOfWhenRightPart(uses: List<T>, getEntityName: (T) -> String) =
        statement("listOf(${uses.joinToString(separator = ", ") { "${getEntityName(it)}$typeSuffix as %T" }})", List(uses.size) { returnParameter })

    /*
     * Get something like this: setOf("invokes[0]", "invokes[1]" ...) -> listOf({uses[0] with typeSuffix} as %T, {uses[1] with typeSuffix} as %T)
     * */
    fun getWhenOptionForSet(invokes: Set<String>, rightPart: CodeBlock): CodeBlock {
        val setOfBlock = if (invokes.isEmpty()) {
            "emptySet<String>()"
        } else {
            "setOf(${invokes.joinToString(separator = ", ") { "\"$it\"" }})"
        }
        return getWhenOption(setOfBlock, rightPart)
    }

    private fun getWhenOptionForString(invoke: String, rightPart: CodeBlock) = getWhenOption("\"$invoke\"", rightPart)

    private fun getWhenOption(leftPart: String, rightPart: CodeBlock) = CodeBlock.builder()
        .add("$leftPart -> ")
        .add(rightPart)
        .build()

    private fun <T> generateWhenBody(
        uses: Iterable<T>,
        conditionVariable: String,
        toAddReturn: Boolean = true,
        generateBranchForWhenOption: (T) -> CodeBlock,
    ): CodeBlock {
        val builder = CodeBlock.builder()
        if (toAddReturn) {
            builder.add("return ")
        }
        builder.beginControlFlow("when (%N)", conditionVariable)
        uses.forEach {
            builder.add(generateBranchForWhenOption(it))
        }
        builder.addStatement("else -> emptyList()")
        builder.endControlFlow()
        return builder.build()
    }

    @Suppress(
        "LAMBDA_IS_NOT_LAST_PARAMETER",
        "IDENTIFIER_LENGTH",
        "TYPE_ALIAS")
    protected fun <K, T> generateWhenBody(
        uses: Map<K, List<T>>,
        conditionVariable: String,
        getEntityName: (T) -> String = { it.toString() },
        toAddReturn: Boolean = true,
        getWhenOption: (K, CodeBlock) -> CodeBlock,
    ): CodeBlock = generateWhenBody(uses.asIterable(), conditionVariable, toAddReturn) { (k, v): Map.Entry<K, List<T>> ->
        getWhenOption(k, listOfWhenRightPart(v, getEntityName))
    }

    @Suppress("TYPE_ALIAS")
    // TODO: group by annotations (store set of signatures for the same set of annotations)
    protected fun generateNestedWhenBodyForFunctions(
        uses: FunctionUses,
        getEntityName: (KtNamedFunction) -> String = { it.toString() },
    ): CodeBlock {
        val namedFunctions = uses.mapValues { (_, namedFunction) -> namedFunction.map { getEntityName(it) } }.toMap().asIterable()

        return generateWhenBody(
            namedFunctions,
            ANNOTATION_FQ_NAMES,
        ) { obj: Map.Entry<SignatureToAnnotations, List<String>> ->
            getWhenOptionForSet(
                obj.key.annotations,
                wrappedCode(
                    generateWhenBody(
                        mapOf(obj.key.signature.stringRepresentation() to obj.value),
                        SIGNATURE,
                        toAddReturn = false,
                        getWhenOption = ::getWhenOptionForString,
                    ),
                ),
            )
        }
    }

    @Suppress("TYPE_ALIAS")
    protected fun generateNestedWhenBodyForClassesOrObjects(uses: ClassOrObjectUses): CodeBlock {
        val classesOrObjects = uses.mapValues { (_, clOrObj) -> clOrObj.mapNotNull { it.fqName?.toString() } }.toMap().asIterable()

        return generateWhenBody(classesOrObjects, ANNOTATION_FQ_NAMES) { obj: Map.Entry<SupertypesToAnnotations, List<String>> ->
            getWhenOptionForSet(
                obj.key.annotations,
                wrappedCode(
                    generateWhenBody(
                        mapOf(obj.key.supertypes to obj.value),
                        SUPERTYPE_FQ_NAMES,
                        toAddReturn = false,
                        getWhenOption = ::getWhenOptionForSet,
                    ),
                ),
            )
        }
    }

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
        const val WITH_SUPERTYPES_FUNCTION_NAME = "withSupertypes"
        val WITH_SUPERTYPES_CLASS_NAME =
            WITH_SUPERTYPES_FUNCTION_NAME.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val WITH_ANNOTATIONS_CLASS_NAME =
            WITH_ANNOTATIONS_FUNCTION_NAME.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // val STRING = KClass::class.asClassName().parameterizedBy(TypeVariableName("T", String::class))
        val SET_OF_STRINGS = Set::class.parameterizedBy(String::class)
    }

    protected abstract inner class WithSupertypesGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_SUPERTYPES_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withSupertypesParameters,
        returnParameter = this.returnParameter,
    )

    protected abstract inner class WithAnnotationsGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withAnnotationsParameters,
        returnParameter = this.returnParameter,
    )
}
