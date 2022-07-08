@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.generation.code.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.kotlin.backend.jvm.codegen.AnnotationCodegen.Companion.annotationClass
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.reflekt.ReflektClass
import org.jetbrains.reflekt.plugin.analysis.common.ReflektPackage
import org.jetbrains.reflekt.plugin.analysis.common.StorageClassNames
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryQueriesResults
import org.jetbrains.reflekt.plugin.analysis.processor.toReflektVisibility
import org.jetbrains.reflekt.plugin.generation.code.generator.models.*
import org.jetbrains.reflekt.plugin.utils.getImmediateSuperclasses
import org.jetbrains.reflekt.plugin.utils.getValueArguments
import java.util.*

/**
 * Generates ReflektImpl.kt file.
 * An example of ReflektImpl.kt file can be found in the reflekt-dsl module.
 *
 * @property libraryQueriesResults [LibraryQueriesResults] that were found in the project
 *  (arguments from all Reflekt queries with entities that satisfy them)
 * @property packageName Reflekt package name
 * @property fileName name of the generated file
 * */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
class ReflektImplGenerator(private val libraryQueriesResults: LibraryQueriesResults) : FileGenerator() {
    override val packageName = ReflektPackage.PACKAGE_NAME
    override val fileName = "ReflektImpl"

    /**
     * The main function to generate the ReflektImpl.kt file content.
     */
    override fun generateImpl() {
        addTypes(ReflektImplClassGenerator().generate())
    }

    /**
     * Generates main ReflektImpl object (see ReflektImpl.kt file in the reflekt-dsl module).
     *
     * @property typeName a fully-qualified class name: org.jetbrains.reflekt.ReflektImpl
     * */
    private inner class ReflektImplClassGenerator : ObjectGenerator() {
        override val typeName = ClassName(packageName, fileName)

        /**
         * The main function to generate ReflektImpl.kt file.
         */
        override fun generateImpl() {
            val innerGenerators = listOf(
                ObjectsGenerator(typeName, libraryQueriesResults.objects),
                ClassesGenerator(typeName, libraryQueriesResults.classes),
                FunctionsGenerator(typeName, libraryQueriesResults.functions, this@ReflektImplGenerator),
            )

            addFunctions(innerGenerators.map { generator ->
                generateFunction(
                    name = generator.typeName.simpleName.replaceFirstChar { it.lowercase(Locale.getDefault()) },
                    body = statement("return %T()", generator.typeName),
                )
            })

            addNestedTypes(innerGenerators.map { it.generate() })

            addReflektClasses()
        }

        fun IrElement.annotationInstantiationString(): String = when (this) {
            is IrConstructorCall -> annotationClass.kotlinFqName.toString() + "(" + getValueArguments().filterNotNull()
                .joinToString(", ") { argument -> argument.annotationInstantiationString() } + ")"
            is IrConst<*> -> value.toString()
            is IrVararg -> elements.joinToString(
                ", ",
                prefix = if (varargElementType.isPrimitiveType()) {
                    "${varargElementType.classFqName!!.shortName()}ArrayOf("
                } else {
                    "arrayOf("
                },
                postfix = ")",
            ) { it.annotationInstantiationString() }

            else -> error("Unsupported annotation argument: $this")
        }

        @Suppress("LongMethod", "TOO_LONG_FUNCTION")
        private fun addReflektClasses() {
            builder.addProperty(
                StorageClassNames.REFLEKT_CLASSES,
                MAP.parameterizedBy(ClassName("kotlin.reflect", "KClass").parameterizedBy(STAR), ReflektClass::class.asTypeName().parameterizedBy(STAR)),
            )

            builder.addInitializerBlock(buildCodeBlock {
                addStatement("val m = HashMap<KClass<*>, ReflektClassImpl<*>>()")

                for (irClass in libraryQueriesResults.mentionedClasses) {
                    with(irClass) {
                        val reflektVisibility = visibility.toReflektVisibility()

                        addStatement(
                            "m[$kotlinFqName::class] = ReflektClassImpl(kClass = $kotlinFqName::class, " +
                                "annotations = hashSetOf(" +
                                annotations.joinToString(", ") { call -> call.annotationInstantiationString() } + "), " +
                                "isAbstract = ${modality == Modality.ABSTRACT}, " +
                                "isCompanion = $isCompanion, " +
                                "isData = $isData, " +
                                "isFinal = ${modality == Modality.FINAL}, " +
                                "isFun = $isFun, isInner = $isInner, " +
                                "isOpen = ${modality == Modality.OPEN}, " +
                                "isSealed = ${modality == Modality.SEALED}, " +
                                "isValue = $isValue, " +
                                "qualifiedName = \"$kotlinFqName\", " +
                                "superclasses = HashSet(), " +
                                "sealedSubclasses = HashSet(), " +
                                "simpleName = \"${kotlinFqName.shortName()}\", " +
                                "visibility = ${reflektVisibility?.let { "ReflektVisibility.${it.name}" } ?: "null"})"
                        )
                    }
                }

                for (mentionedClass in libraryQueriesResults.mentionedClasses) {
                    mentionedClass.getImmediateSuperclasses().map { it.owner }.forEach { superclass ->
                        addStatement(
                            "(m[${mentionedClass.kotlinFqName}::class]!! as ReflektClassImpl<${mentionedClass.kotlinFqName}>).superclasses += " +
                                "m[${superclass.kotlinFqName}::class] as ReflektClass<in ${mentionedClass.kotlinFqName}>"
                        )
                    }
                }

                for (mentionedClass in libraryQueriesResults.mentionedClasses) {
                    mentionedClass.sealedSubclasses.map { it.owner }.forEach { subclass ->
                        addStatement(
                            "(m[${mentionedClass.kotlinFqName}::class]!! as ReflektClassImpl<${mentionedClass.kotlinFqName}>).sealedSubclasses += " +
                                "m[${subclass.kotlinFqName}::class] as ReflektClass<out ${mentionedClass.kotlinFqName}>"
                        )
                    }
                }

                addStatement("${StorageClassNames.REFLEKT_CLASSES} = m")
            })
        }
    }
}
