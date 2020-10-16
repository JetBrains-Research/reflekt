package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.reflekt.plugin.generator.generateFunction
import io.reflekt.plugin.generator.singleLineCode
import io.reflekt.plugin.generator.toParameterSpecs

abstract class HelperClassGenerator : ClassGenerator() {
    abstract val typeVariable: TypeVariableName
    abstract val returnParameter: TypeName

    open val withSubTypesFunctionBody: CodeBlock
        get() = singleLineCode(
            "return %T(%N)",
            typeName.nestedClass(WITH_SUBTYPES_CLASS_NAME).parameterizedBy(typeVariable),
            FQ_NAMES
        )

    open val withAnnotationsFunctionBody: CodeBlock
        get() = singleLineCode(
            "return %T(%N, %N)",
            typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME).parameterizedBy(typeVariable),
            ANNOTATION_FQ_NAMES,
            SUBTYPE_FQ_NAMES
        )

    open val withSubTypesParameters = mapOf(
        FQ_NAMES to SET_OF_STRINGS
    ).toParameterSpecs()

    open val withAnnotationsParameters = mapOf(
        ANNOTATION_FQ_NAMES to SET_OF_STRINGS,
        SUBTYPE_FQ_NAMES to SET_OF_STRINGS
    ).toParameterSpecs()

    fun generateWithSubTypesFunction() {
        builder.addFunction(generateFunction(
            name = WITH_SUBTYPES_FUNCTION_NAME,
            body = withSubTypesFunctionBody,
            typeVariables = listOf(typeVariable),
            arguments = withSubTypesParameters
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

    protected abstract inner class WithSubTypesGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_SUBTYPES_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withSubTypesParameters,
        returnParameter = this.returnParameter
    )

    protected abstract inner class WithAnnotationsGenerator : SelectorClassGeneratorWrapper(
        typeName = this.typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME),
        typeVariable = this.typeVariable,
        parameters = this.withAnnotationsParameters,
        returnParameter = this.returnParameter
    )

    protected companion object {
        const val WITH_SUBTYPES_FUNCTION_NAME = "withSubTypes"
        val WITH_SUBTYPES_CLASS_NAME = WITH_SUBTYPES_FUNCTION_NAME.capitalize()

        const val WITH_ANNOTATIONS_FUNCTION_NAME = "withAnnotations"
        val WITH_ANNOTATIONS_CLASS_NAME = WITH_ANNOTATIONS_FUNCTION_NAME.capitalize()

        const val FQ_NAMES = "fqNames"
        const val ANNOTATION_FQ_NAMES = "annotationFqNames"
        const val SUBTYPE_FQ_NAMES = "subtypeFqNames"

        const val UNKNOWN_FQ_NAME = "Unknown fully qualified names set"

        val SET_OF_STRINGS = Set::class.parameterizedBy(String::class)
    }
}
