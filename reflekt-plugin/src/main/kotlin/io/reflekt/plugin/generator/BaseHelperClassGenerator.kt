package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

abstract class BaseHelperClassGenerator : ClassGenerator() {
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

    protected abstract inner class WithSubTypesGenerator : BaseSelectorClassGenerator() {
        override val typeName = this@BaseHelperClassGenerator.typeName.nestedClass(WITH_SUBTYPES_CLASS_NAME)
        override val typeVariable = this@BaseHelperClassGenerator.typeVariable
        override val parameters = this@BaseHelperClassGenerator.withSubTypesParameters
        override val returnParameter = this@BaseHelperClassGenerator.returnParameter
    }

    protected abstract inner class WithAnnotationsGenerator : BaseSelectorClassGenerator() {
        override val typeName = this@BaseHelperClassGenerator.typeName.nestedClass(WITH_ANNOTATIONS_CLASS_NAME)
        override val typeVariable = this@BaseHelperClassGenerator.typeVariable
        override val parameters = this@BaseHelperClassGenerator.withAnnotationsParameters
        override val returnParameter = this@BaseHelperClassGenerator.returnParameter
    }

    protected companion object {
        const val WITH_SUBTYPES_FUNCTION_NAME = "withSubTypes"
        val WITH_SUBTYPES_CLASS_NAME = WITH_SUBTYPES_FUNCTION_NAME.capitalize()

        const val WITH_ANNOTATIONS_FUNCTION_NAME = "withAnnotations"
        val WITH_ANNOTATIONS_CLASS_NAME = WITH_ANNOTATIONS_FUNCTION_NAME.capitalize()

        const val FQ_NAMES = "fqNames"
        const val ANNOTATION_FQ_NAMES = "annotationFqNames"
        const val SUBTYPE_FQ_NAMES = "subtypeFqNames"

        val SET_OF_STRINGS = Set::class.parameterizedBy(String::class)
    }
}
