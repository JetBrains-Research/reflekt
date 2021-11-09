package io.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec

abstract class FileGenerator : Generator<String>() {
    protected abstract val packageName: String
    protected abstract val fileName: String
    private val aliases: MutableMap<String, Int> = HashMap()
    private lateinit var builder: FileSpec.Builder

    final override fun initBuilder() {
        builder = FileSpec.builder(packageName, fileName).indent(indent)
    }

    final override fun generate(): String {
        initBuilder()
        generateImpl()
        return builder.build().toString()
    }

    fun addTypes(vararg types: TypeSpec) {
        for (type in types) {
            builder.addType(type)
        }
    }

    fun addFunctions(vararg functions: FunSpec) {
        for (function in functions) {
            builder.addFunction(function)
        }
    }

    fun addUniqueAliasedImport(memberName: MemberName): String {
        val index = aliases.getOrPut(memberName.simpleName, { 1 })
        val alias = "${memberName.simpleName}N$index"
        aliases[memberName.simpleName] = index + 1
        addAliasedImport(memberName, alias)
        return alias
    }

    private fun addAliasedImport(memberName: MemberName, alias: String) {
        builder.addAliasedImport(memberName, alias)
    }

    companion object {
        var indent: String = " ".repeat(4)
    }
}
