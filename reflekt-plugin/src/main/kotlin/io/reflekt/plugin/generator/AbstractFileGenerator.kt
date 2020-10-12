package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

abstract class AbstractFileGenerator {
    protected abstract val packageName: String
    protected abstract val fileName: String

    private lateinit var builder: FileSpec.Builder

    private fun initBuilder() {
        builder = FileSpec.builder(packageName, fileName).indent(indent)
    }

    abstract fun generateImpl()

    fun generate(): String {
        initBuilder()
        generateImpl()
        return builder.build().toString()
    }

    fun addType(type: TypeSpec) {
        builder.addType(type)
    }

    fun addFunction(function: FunSpec) {
        builder.addFunction(function)
    }

    fun setIndent(indent: String) {
        AbstractFileGenerator.indent = indent
    }

    private companion object {
        var indent: String = " ".repeat(4)
    }
}
