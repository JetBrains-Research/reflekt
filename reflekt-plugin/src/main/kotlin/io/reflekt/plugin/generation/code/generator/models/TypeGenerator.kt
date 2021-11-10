package io.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

abstract class TypeGenerator : Generator<TypeSpec>() {
    abstract val typeName: ClassName
    protected lateinit var builder: TypeSpec.Builder

    final override fun generate(): TypeSpec {
        initBuilder()
        generateImpl()
        return builder.build()
    }

    fun addNestedTypes(vararg nestedTypes: TypeSpec) {
        addNestedTypes(listOf(*nestedTypes))
    }

    fun addNestedTypes(nestedTypes: Iterable<TypeSpec>) {
        builder.addTypes(nestedTypes)
    }

    fun addFunctions(vararg functions: FunSpec) {
        addFunctions(listOf(*functions))
    }

    fun addFunctions(functions: Iterable<FunSpec>) {
        builder.addFunctions(functions)
    }
}

abstract class ClassGenerator : TypeGenerator() {
    override fun initBuilder() {
        builder = TypeSpec.classBuilder(typeName)
    }
}

abstract class ObjectGenerator : TypeGenerator() {
    override fun initBuilder() {
        builder = TypeSpec.objectBuilder(typeName)
    }
}
