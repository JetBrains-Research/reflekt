package org.jetbrains.reflekt.plugin.generation.code.generator.models

import com.squareup.kotlinpoet.*

/**
 * A base class to generate class, object, interface, or enum declaration
 *
 * @property typeName a fully-qualified class name
 * @property builder a special builder to build the class, object, interface, or enum declaration
 */
@Suppress("KDOC_NO_CLASS_BODY_PROPERTIES_IN_HEADER", "KDOC_EXTRA_PROPERTY")
abstract class TypeGenerator : Generator<TypeSpec>() {
    abstract val typeName: ClassName
    protected lateinit var builder: TypeSpec.Builder

    /**
     * Generate class, object, interface, or enum declaration
     *
     * @return a generated class, interface, or enum declaration
     */
    final override fun generate(): TypeSpec {
        initBuilder()
        generateImpl()
        return builder.build()
    }

    /**
     * Add nested class, object, interface, or enum declaration
     *
     * @param nestedTypes
     */
    fun addNestedTypes(vararg nestedTypes: TypeSpec) {
        addNestedTypes(listOf(*nestedTypes))
    }

    /**
     * Add nested class, object, interface, or enum declaration
     *
     * @param nestedTypes
     */
    fun addNestedTypes(nestedTypes: Iterable<TypeSpec>) {
        builder.addTypes(nestedTypes)
    }

    /**
     * Add functions to the generated class, object, interface, or enum declaration
     *
     * @param functions
     */
    fun addFunctions(vararg functions: FunSpec) {
        addFunctions(listOf(*functions))
    }

    /**
     * Add functions to the generated class, object, interface, or enum declaration
     *
     * @param functions
     */
    fun addFunctions(functions: Iterable<FunSpec>) {
        builder.addFunctions(functions)
    }
}

/**
 * An abstract to generate a new class
 */
abstract class ClassGenerator : TypeGenerator() {
    /**
     * Specify the builder to generate the class
     */
    override fun initBuilder() {
        builder = TypeSpec.classBuilder(typeName)
    }
}

/**
 * An abstract to generate a new object
 */
abstract class ObjectGenerator : TypeGenerator() {
    /**
     * Specify the builder to generate the object
     */
    override fun initBuilder() {
        builder = TypeSpec.objectBuilder(typeName)
    }
}
