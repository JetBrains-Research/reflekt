package org.jetbrains.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec

/**
 * A generator for generating a new kt file with
 *
 * @property packageName the file's package name
 * @property fileName name of the generated file
 * @property aliases unique aliases counte, e.g. for imported functions
 * @property builder a builder to build a new kt file
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
abstract class FileGenerator : Generator<String>() {
    protected abstract val packageName: String
    protected abstract val fileName: String
    private val aliases: MutableMap<String, Int> = HashMap()
    private lateinit var builder: FileSpec.Builder

    /**
     * Initialization of the main file builder
     */
    final override fun initBuilder() {
        builder = FileSpec.builder(packageName, fileName).indent(indent)
    }

    /**
     * The main function to generate new kt file
     */
    final override fun generate(): String {
        initBuilder()
        generateImpl()
        return builder.build().toString()
    }

    /**
     * Add generated classes, interfaces, or enum declarations
     *
     * @param types generated classes, interfaces, or enum declarations
     */
    fun addTypes(vararg types: TypeSpec) {
        for (type in types) {
            builder.addType(type)
        }
    }

    /**
     * Add generated function declarations
     *
     * @param functions generated function declarations
     */
    fun addFunctions(vararg functions: FunSpec) {
        for (function in functions) {
            builder.addFunction(function)
        }
    }

    /**
     * Add new unique aliased import for members such as a function or a property
     *
     * @param memberName
     * @return string representation of the unique aliased import
     */
    fun addUniqueAliasedImport(memberName: MemberName): String {
        val index = aliases.getOrPut(memberName.simpleName) { 1 }
        val alias = "${memberName.simpleName}N$index"
        aliases[memberName.simpleName] = index + 1
        addAliasedImport(memberName, alias)
        return alias
    }

    /**
     * Add new [alias] to the [builder] for the [memberName]
     *
     * @param memberName
     * @param alias
     */
    private fun addAliasedImport(memberName: MemberName, alias: String) {
        builder.addAliasedImport(memberName, alias)
    }

    companion object {
        var indent: String = " ".repeat(4)
    }
}
