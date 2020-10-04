package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import io.reflekt.plugin.analysis.Invokes
import io.reflekt.plugin.analysis.ReflektAnalyzer
import io.reflekt.plugin.generator.GeneratorConstants.classesObject
import io.reflekt.plugin.generator.GeneratorConstants.fileName
import io.reflekt.plugin.generator.GeneratorConstants.objectsObject
import io.reflekt.plugin.generator.GeneratorConstants.packageName
import io.reflekt.plugin.generator.GeneratorConstants.reflektImplObject

fun generateReflektImpl(invokes: Invokes, analyzer: ReflektAnalyzer): String
    = FileSpec.builder(packageName, fileName)
    .addImport("kotlin.reflect", "KClass")
    .addType(TypeSpec.objectBuilder(reflektImplObject)
        .addType(ClassesOrObjectsGenerator(
            objectName = objectsObject,
            withSubTypeNames = invokes.withSubTypeObjects,
            withAnnotationNames = invokes.withAnnotationObjects,
            analyzer = analyzer::objects,
            withSubTypeTypeVariable = TypeVariableName("T"),
            withAnnotationTypeVariable = TypeVariableName("T"),
            returnTypeVariable = TypeVariableName("T"),
            asSuffix = " as T"
        ).generate())
        .addType(ClassesOrObjectsGenerator(
            classesObject,
            invokes.withSubTypeClasses,
            invokes.withAnnotationClasses,
            analyzer::classes,
            withSubTypeTypeVariable = TypeVariableName("T", Any::class),
            withAnnotationTypeVariable = TypeVariableName("T", Annotation::class),
            returnTypeVariable = TypeVariableName("KClass<T>"),
            asSuffix = "::class as KClass<T>"
        ).generate())
        .addFunction(FunSpec.builder("objects")
            .addStatement("return %T", objectsObject)
            .build())
        .addFunction(FunSpec.builder("classes")
            .addStatement("return %T", classesObject)
            .build())
        .build())
    .build()
    .toString()
