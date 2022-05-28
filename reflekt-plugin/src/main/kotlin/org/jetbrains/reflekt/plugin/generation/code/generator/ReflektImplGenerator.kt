@file:Suppress("FILE_UNORDERED_IMPORTS")

package org.jetbrains.reflekt.plugin.generation.code.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.reflekt.ReflektClass
import org.jetbrains.reflekt.plugin.analysis.models.ir.LibraryQueriesResults
import org.jetbrains.reflekt.plugin.generation.code.generator.models.*
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
    override val packageName = "org.jetbrains.reflekt"
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

            builder.addProperty(
                "reflektClasses",
                MAP.parameterizedBy(ClassName("kotlin.reflect", "KClass").parameterizedBy(STAR), ReflektClass::class.asTypeName().parameterizedBy(STAR)),
            )

            builder.addInitializerBlock(buildCodeBlock {
                addStatement("val m = HashMap<KClass<*>, ReflektClass<*>>()")

                innerGenerators.filterIsInstance<ClassesGenerator>().flatMapTo(HashSet()) { it.mentionedClasses }.forEach { name ->
                    val clazz = libraryQueriesResults.mentionedClasses.find { clazz -> clazz.qualifiedName == name } ?: error("Can't find class data for $name")

                    addStatement(
                        "m[$name::class] = ReflektClassImpl(" +
                            "kClass = $name::class, " +
                            "annotations = ${clazz.annotations.joinToString(separator = ", ", prefix = "hashSetOf(", postfix = ")") { "$it::class" }}, " +
                            "isAbstract = ${clazz.isAbstract}, " +
                            "isCompanion = ${clazz.isCompanion}, " +
                            "isData = ${clazz.isData}, " +
                            "isFinal = ${clazz.isFinal}, " +
                            "isFun = ${clazz.isFun}, " +
                            "isInner = ${clazz.isInner}, " +
                            "isOpen = ${clazz.isOpen}, " +
                            "isSealed = ${clazz.isSealed}, " +
                            "isValue = ${clazz.isValue}, " +
                            "qualifiedName = \"${clazz.qualifiedName}\", " +
                            "superclasses = HashSet(), " +
                            "sealedSubclasses = HashSet(), " +
                            "simpleName = \"${clazz.simpleName}\", " +
                            "visibility = ${if (clazz.visibility == null) "null" else "kotlin.reflect.KVisibility.${clazz.visibility.name}"}" +
                            ")"
                    )
                }

                addStatement("reflektClasses = m")
            })
        }
    }
}
