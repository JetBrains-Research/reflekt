package io.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.SupertypesToAnnotations
import io.reflekt.plugin.generation.code.generator.*
import org.jetbrains.kotlin.psi.KtClassOrObject

abstract class HelperClassGenerator : ClassGenerator() {
    abstract val typeVariable: TypeVariableName
    abstract val returnParameter: TypeName
    protected open val typeSuffix: String = ""

    open val withSupertypesFunctionBody: CodeBlock
        get() = statement(
            "return %T(%N)",
            typeName.nestedClass(WITH_SUPERTYPES_CLASS_NAME).parameterizedBy(typeVariable),
            FQ_NAMES
        )

    open val withAnnotationsFunctionBody: CodeBlock
        get() = statement(
            "return %T(%N, %N)",
            typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME).parameterizedBy(typeVariable),
            ANNOTATION_FQ_NAMES,
            SUPERTYPE_FQ_NAMES
        )

    open val withSupertypesParameters = mapOf(
        FQ_NAMES to SET_OF_STRINGS
    ).toParameterSpecs()

    open val withAnnotationsParameters = mapOf(
        ANNOTATION_FQ_NAMES to SET_OF_STRINGS,
        SUPERTYPE_FQ_NAMES to SET_OF_STRINGS
    ).toParameterSpecs()

    fun generateWithSupertypesFunction() {
        builder.addFunction(generateFunction(
            name = WITH_SUPERTYPES_FUNCTION_NAME,
            body = withSupertypesFunctionBody,
            typeVariables = listOf(typeVariable),
            arguments = withSupertypesParameters
        ))
    }

    fun generateWithAnnotationsFunction() {
        builder.addFunction(generateFunction(
            name = WITH_ANNOTATIONS_FUNCTION_NAME,
            body = withAnnotationsFunctionBody,
            typeVariables = listOf(typeVariable),
            arguments = withAnnotationsParameters
        ))
    }

    protected abstract class SelectorClassGeneratorWrapper(
        override val typeName: ClassName,
        override val typeVariable: TypeVariableName,
        override val parameters: List<ParameterSpec>,
        override val returnParameter: TypeName
    ) : SelectorClassGenerator()

    protected abstract inner class WithSupertypesGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_SUPERTYPES_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withSupertypesParameters,
        returnParameter = this.returnParameter
    )

    protected abstract inner class WithAnnotationsGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withAnnotationsParameters,
        returnParameter = this.returnParameter
    )

    private fun <T> listOfWhenRightPart(uses: List<T>, getEntityName: (T) -> String) =
        statement("listOf(${uses.joinToString(separator = ", ") { "${getEntityName(it)}$typeSuffix as %T" }})", List(uses.size) { returnParameter })

    /*
     * Get something like this: setOf("invokes[0]", "invokes[1]" ...) -> listOf({uses[0] with typeSuffix} as %T, {uses[1] with typeSuffix} as %T)
     * */
    private fun getWhenOption(invokes: Set<String>, rightPart: CodeBlock): CodeBlock {
        return CodeBlock.builder()
            .add("setOf(${invokes.joinToString(separator = ", ") { "\"$it\"" }}) -> ")
            .add(rightPart)
            .build()
    }

    private fun <T> generateWhenBody(uses: Iterable<T>, conditionVariable: String, generateBranchForWhenOption: (T) -> CodeBlock, toAddReturn: Boolean = true): CodeBlock {
        val builder = CodeBlock.builder()
        if (toAddReturn) {
            builder.add("return ")
        }
        builder.beginControlFlow("when (%N)", conditionVariable)
        uses.forEach{
            builder.add(generateBranchForWhenOption(it))
        }
        builder.addStatement("else -> error(%S)", UNKNOWN_FQ_NAME)
        builder.endControlFlow()
        return builder.build()
    }

    protected fun <T> generateWhenBody(
        uses: Map<Set<String>, List<T>>, conditionVariable: String, getEntityName: (T) -> String = { it.toString() }, toAddReturn: Boolean = true
    ): CodeBlock {
        val generateBranchForWhenOption = { (k, v): Map.Entry<Set<String>, List<T>> -> getWhenOption(k, listOfWhenRightPart(v, getEntityName)) }
        return generateWhenBody(uses.asIterable(), conditionVariable, generateBranchForWhenOption, toAddReturn)
    }

    protected fun generateNestedWhenBody(uses: ClassOrObjectUses, annotationFqNames: String, supertypeFqNames: String): CodeBlock {
        val mainFunction = { o: Map.Entry<SupertypesToAnnotations, MutableList<KtClassOrObject>> ->
            getWhenOption(o.key.annotations, wrappedCode(generateWhenBody(mapOf(o.key.supertypes to o.value), supertypeFqNames, toAddReturn = false)))
        }
        return generateWhenBody(uses.toMap().asIterable(), annotationFqNames, mainFunction)
    }

    protected companion object {
        const val WITH_SUPERTYPES_FUNCTION_NAME = "withSupertypes"
        val WITH_SUPERTYPES_CLASS_NAME = WITH_SUPERTYPES_FUNCTION_NAME.capitalize()

        const val WITH_ANNOTATIONS_FUNCTION_NAME = "withAnnotations"
        val WITH_ANNOTATIONS_CLASS_NAME = WITH_ANNOTATIONS_FUNCTION_NAME.capitalize()

        const val FQ_NAMES = "fqNames"
        const val ANNOTATION_FQ_NAMES = "annotationFqNames"
        const val SUPERTYPE_FQ_NAMES = "supertypeFqNames"

        const val UNKNOWN_FQ_NAME = "Unknown fully qualified names set"

        val SET_OF_STRINGS = Set::class.parameterizedBy(String::class)
    }
}
