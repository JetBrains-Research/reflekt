package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

abstract class FileGenerator : Generator<String>() {
    protected abstract val packageName: String
    protected abstract val fileName: String

    companion object {
        private var indent: String = " ".repeat(4)

        fun setIndent(indent: String) {
            this.indent = indent
        }
    }

    private lateinit var builder: FileSpec.Builder

    final override fun initBuilder() {
        builder = FileSpec.builder(packageName, fileName).indent(indent)
    }

    final override fun generate(): String {
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
}
