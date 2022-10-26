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
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.utils.addToStdlib.flattenTo
import org.jetbrains.reflekt.ReflektClass
import org.jetbrains.reflekt.ReflektFunction
import org.jetbrains.reflekt.plugin.analysis.common.ReflektPackage
import org.jetbrains.reflekt.plugin.analysis.common.StorageClassNames
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryQueriesResults
import org.jetbrains.reflekt.plugin.analysis.processor.toReflektVisibility
import org.jetbrains.reflekt.plugin.generation.code.generator.models.*
import org.jetbrains.reflekt.plugin.utils.getImmediateSuperclasses
import org.jetbrains.reflekt.plugin.utils.getValueArguments
import java.util.*
import kotlin.reflect.KClass

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

            if (libraryQueriesResults.mentionedClasses.isNotEmpty()) {
                addReflektClasses()
            }

            if (libraryQueriesResults.functions.isNotEmpty()) {
                addReflektFunctions()
            }
        }

        /**
         * Creates string for constructor call of provided annotation in the form [IrConstructorCall] like `MyAnnotation(1, "321", OtherAnnotation())`.
         */
        private fun annotationInstantiationString(irElement: IrElement): String = when (irElement) {
            is IrConstructorCall -> irElement.annotationClass.kotlinFqName.toString() + "(" + irElement.getValueArguments().filterNotNull()
                .joinToString(", ") { argument -> annotationInstantiationString(argument) } + ")"

            is IrConst<*> -> when (irElement.kind) {
                IrConstKind.String -> "\"${irElement.value}\""
                else -> irElement.value.toString()
            }

            is IrVararg -> irElement.elements.joinToString(
                ", ",
                prefix = if (irElement.varargElementType.isPrimitiveType()) {
                    "${irElement.varargElementType.classFqName!!.shortName().toString().lowercase()}ArrayOf("
                } else {
                    "arrayOf("
                },
                postfix = ")",
            ) { annotationInstantiationString(it) }

            else -> error("Unsupported annotation argument: $irElement")
        }

        @Suppress("LongMethod", "TOO_LONG_FUNCTION")
        private fun addReflektClasses() {
            builder.addProperty(
                StorageClassNames.REFLEKT_CLASSES,
                MAP.parameterizedBy(KClass::class.asTypeName().parameterizedBy(STAR), ReflektClass::class.asTypeName().parameterizedBy(STAR)),
            )

            builder.addInitializerBlock(buildCodeBlock {
                addStatement("val m = HashMap<KClass<*>, ReflektClassImpl<*>>()")

                for (irClass in libraryQueriesResults.mentionedClasses) {
                    with(irClass) {
                        val reflektVisibility = checkNotNull(visibility.toReflektVisibility()) { "Unsupported visibility of IrClass: $visibility" }

                        addStatement(
                            "m[$kotlinFqName::class] = ReflektClassImpl(kClass = $kotlinFqName::class, " +
                                "annotations = hashSetOf(${annotations.joinToString(", ") { call -> annotationInstantiationString(call) }}), " +
                                "isAbstract = ${modality == Modality.ABSTRACT}, " +
                                "isCompanion = $isCompanion, " +
                                "isData = $isData, " +
                                "isFinal = ${modality == Modality.FINAL}, " +
                                "isFun = $isFun, isInner = $isInner, " +
                                "isOpen = ${modality == Modality.OPEN}, " +
                                "isSealed = ${modality == Modality.SEALED}, " +
                                "isValue = $isValue, " +
                                "qualifiedName = \"$kotlinFqName\", " +
                                "simpleName = \"${kotlinFqName.shortName()}\", " +
                                "visibility = ReflektVisibility.${reflektVisibility.name}, " +
                                "objectInstance = ${if (isObject) kotlinFqName.toString() else null})",
                        )
                    }
                }

                for (mentionedClass in libraryQueriesResults.mentionedClasses) {
                    mentionedClass.getImmediateSuperclasses().map { it.owner }.forEach { superclass ->
                        addStatement(
                            "(m[${mentionedClass.kotlinFqName}::class]!! as ReflektClassImpl<${mentionedClass.kotlinFqName}>).superclasses += " +
                                "m[${superclass.kotlinFqName}::class] as ReflektClass<in ${mentionedClass.kotlinFqName}>",
                        )
                    }
                }

                for (mentionedClass in libraryQueriesResults.mentionedClasses) {
                    mentionedClass.sealedSubclasses.map { it.owner }.forEach { subclass ->
                        addStatement(
                            "(m[${mentionedClass.kotlinFqName}::class]!! as ReflektClassImpl<${mentionedClass.kotlinFqName}>).sealedSubclasses += " +
                                "m[${subclass.kotlinFqName}::class] as ReflektClass<out ${mentionedClass.kotlinFqName}>",
                        )
                    }
                }

                addStatement("${StorageClassNames.REFLEKT_CLASSES} = m")
            })
        }

        private fun addReflektFunctions() {
            builder.addProperty(
                StorageClassNames.REFLEKT_FUNCTIONS,
                MAP.parameterizedBy(Function::class.asTypeName().parameterizedBy(STAR), ReflektFunction::class.asTypeName().parameterizedBy(STAR)),
            )

            builder.addInitializerBlock(buildCodeBlock {
                addStatement("val m = HashMap<Function<*>, ReflektFunctionImpl<*>>()")

                for (irFunction in libraryQueriesResults.functions.values.flattenTo(mutableSetOf())) {
                    with(irFunction) {
                        val reference = FunctionsGenerator.functionReference(this@ReflektImplGenerator, this)
                        val reflektVisibility = checkNotNull(visibility.toReflektVisibility()) { "Unsupported visibility of IrFunction: $visibility" }

                        addStatement(
                            "m[$reference] = ReflektFunctionImpl(" +
                                "function = $reference, " +
                                "annotations = hashSetOf(${annotations.joinToString(", ") { call -> annotationInstantiationString(call) }}), " +
                                "name = \"a\", " +
                                "visibility = ReflektVisibility.${reflektVisibility.name}, " +
                                "isFinal = ${modality == Modality.FINAL}, " +
                                "isOpen = ${modality == Modality.OPEN}, " +
                                "isAbstract = ${modality == Modality.ABSTRACT}, " +
                                "isInline = $isInline, " +
                                "isExternal = $isExternal, " +
                                "isOperator = $isOperator, " +
                                "isInfix = $isInfix, " +
                                "isSuspend = $isSuspend)",
                        )
                    }
                }

                addStatement("${StorageClassNames.REFLEKT_FUNCTIONS} = m")
            })
        }
    }
}
