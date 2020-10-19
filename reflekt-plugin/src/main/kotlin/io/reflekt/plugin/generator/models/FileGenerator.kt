package io.reflekt.plugin.generator.models

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

abstract class FileGenerator : Generator<String>() {
    protected abstract val packageName: String
    protected abstract val fileName: String

    companion object {
        var indent: String = " ".repeat(4)
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
}
