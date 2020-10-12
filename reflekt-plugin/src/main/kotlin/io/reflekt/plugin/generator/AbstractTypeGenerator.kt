package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

abstract class AbstractTypeGenerator {
    abstract val typeName: ClassName

    protected lateinit var builder: TypeSpec.Builder

    abstract fun initBuilder()
    abstract fun generateImpl()

    fun generate(): TypeSpec {
        initBuilder()
        generateImpl()
        return builder.build()
    }

    fun addNestedType(nestedType: TypeSpec) {
        builder.addType(nestedType)
    }

    fun addNestedTypes(nestedTypes: Iterable<TypeSpec>) {
        builder.addTypes(nestedTypes)
    }

    fun addFunction(function: FunSpec) {
        builder.addFunction(function)
    }

    fun addFunctions(functions: Iterable<FunSpec>) {
        builder.addFunctions(functions)
    }
}

abstract class ClassGenerator : AbstractTypeGenerator() {
    override fun initBuilder() {
        builder = TypeSpec.classBuilder(typeName)
    }
}

abstract class ObjectGenerator : AbstractTypeGenerator() {
    override fun initBuilder() {
        builder = TypeSpec.objectBuilder(typeName)
    }
}
