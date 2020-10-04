package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.ClassName

object GeneratorConstants {
    const val fileName = "ReflektImpl"
    const val packageName = "io.reflekt"

    const val withSubTypeFunctionName = "withSubType"
    const val withSubTypeClassName = "WithSubType"

    const val withAnnotationFunctionName = "withAnnotation"
    const val withAnnotationClassName = "WithAnnotation"

    const val toListFunctionName = "toList"
    const val toSetFunctionName = "toSet"

    const val qualifiedName = "fqName"
    const val subtypeQualifiedName = "subtypeFqName"

    val reflektImplObject = ClassName(packageName, "ReflektImpl")
    val objectsObject = reflektImplObject.nestedClass("Objects")
    val classesObject = reflektImplObject.nestedClass("Classes")
}
