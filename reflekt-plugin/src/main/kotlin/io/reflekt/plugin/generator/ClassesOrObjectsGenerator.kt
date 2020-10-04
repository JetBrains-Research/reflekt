package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.reflekt.plugin.generator.GeneratorConstants.qualifiedName
import io.reflekt.plugin.generator.GeneratorConstants.toListFunctionName
import io.reflekt.plugin.generator.GeneratorConstants.toSetFunctionName
import io.reflekt.plugin.generator.GeneratorConstants.withAnnotationClassName
import io.reflekt.plugin.generator.GeneratorConstants.withAnnotationFunctionName
import io.reflekt.plugin.generator.GeneratorConstants.withSubTypeClassName
import io.reflekt.plugin.generator.GeneratorConstants.withSubTypeFunctionName
import io.reflekt.plugin.generator.GeneratorConstants.subtypeQualifiedName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext

class ClassesOrObjectsGenerator(
    private val objectName: ClassName,
    private val withSubTypeNames: Set<String>,
    private val withAnnotationNames: MutableMap<String, MutableList<String>>,
    private val analyzer: ((KtClassOrObject, BindingContext) -> Boolean) -> Set<KtClassOrObject>,
    private val withSubTypeTypeVariable: TypeVariableName,
    private val withAnnotationTypeVariable: TypeVariableName,
    private val returnTypeVariable: TypeVariableName,
    private val asSuffix: String
) {
    private val withSubTypeClass = objectName.nestedClass(withSubTypeClassName)
    private val withAnnotationClass = withSubTypeClass.nestedClass(withAnnotationClassName)

    fun generate(): TypeSpec = TypeSpec.objectBuilder(objectName)
        .addFunction(generateWithSubTypeFunction())
        .addType(WithSubTypeClassGenerator().generate())
        .build()

    private fun generateWithSubTypeFunction(): FunSpec = FunSpec.builder(withSubTypeFunctionName)
            .addTypeVariable(withSubTypeTypeVariable)
            .addParameter(qualifiedName, String::class)
            .addStatement("return %T<%T>(%N)", withSubTypeClass, withSubTypeTypeVariable, qualifiedName)
            .build()

    private inner class WithSubTypeClassGenerator {
        fun generate(): TypeSpec = TypeSpec.classBuilder(withSubTypeClassName)
            .addTypeVariable(withSubTypeTypeVariable)
            .primaryConstructor(FunSpec.constructorBuilder()
                .addParameter(qualifiedName, String::class)
                .build())
            .addProperty(PropertySpec.builder(qualifiedName, String::class)
                .initializer(qualifiedName)
                .build())
            .addFunction(generateToListFunction())
            .addFunction(generateToSetFunction())
            .addFunction(generateWithAnnotationFunction())
            .addType(WithAnnotationClassGenerator().generate())
            .build()

        fun generateToListFunction(): FunSpec = FunSpec.builder(toListFunctionName)
            .returns(ClassName("kotlin.collections", "List").parameterizedBy(returnTypeVariable))
            .beginControlFlow("return when (%N)", qualifiedName)
            .addCode(getWhenBodyForInvokes(withSubTypeNames, analyzer, asSuffix))
            .addStatement("else -> error(%S)", "Unknown $qualifiedName")
            .endControlFlow()
            .build()

        fun generateToSetFunction(): FunSpec = FunSpec.builder(toSetFunctionName)
            .returns(ClassName("kotlin.collections", "Set").parameterizedBy(returnTypeVariable))
            .addStatement("return %N().%N()", toListFunctionName, toSetFunctionName)
            .build()

        fun generateWithAnnotationFunction(): FunSpec = FunSpec.builder(withAnnotationFunctionName)
            .addTypeVariable(withAnnotationTypeVariable)
            .addParameter(qualifiedName, String::class)
            .addParameter(subtypeQualifiedName, String::class)
            .addStatement("return %T<%T>(%N, %N)", withAnnotationClass, withAnnotationTypeVariable, qualifiedName, subtypeQualifiedName)
            .build()

        inner class WithAnnotationClassGenerator {
            fun generate(): TypeSpec = TypeSpec.classBuilder(withAnnotationClass)
                .addTypeVariable(withAnnotationTypeVariable)
                .primaryConstructor(FunSpec.constructorBuilder()
                    .addParameter(qualifiedName, String::class)
                    .addParameter(subtypeQualifiedName, String::class)
                    .build())
                .addProperty(PropertySpec.builder(qualifiedName, String::class)
                    .initializer(qualifiedName)
                    .build())
                .addProperty(PropertySpec.builder(subtypeQualifiedName, String::class)
                    .initializer(subtypeQualifiedName)
                    .build())
                .addFunction(generateToListFunction())
                .addFunction(generateToSetFunction())
                .build()

            fun generateToListFunction(): FunSpec = FunSpec.builder(toListFunctionName)
                .returns(ClassName("kotlin.collections", "List").parameterizedBy(returnTypeVariable))
                .beginControlFlow("return when (%N)", subtypeQualifiedName)
                .addCode(getWhenBodyForInvokes(withAnnotationNames, analyzer, asSuffix))
                .addStatement("else -> error(%S)", "Unknown $subtypeQualifiedName")
                .endControlFlow()
                .build()

            fun generateToSetFunction(): FunSpec = FunSpec.builder(toSetFunctionName)
                .returns(ClassName("kotlin.collections", "Set").parameterizedBy(returnTypeVariable))
                .addStatement("return %N().%N()", toListFunctionName, toSetFunctionName)
                .build()
        }
    }
}
