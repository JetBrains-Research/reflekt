package org.jetbrains.reflekt.plugin.generation.code.generator.models

/**
 * A base class for all code generators.
 */
abstract class Generator<out T> {
    /**
     * Initialization of the main builder (e.g. [com.squareup.kotlinpoet.TypeSpec.Builder] or [com.squareup.kotlinpoet.FileSpec.Builder]) for the generator,
     *  the function can be empty if the builder does not exist for the current generator.
     *
     * Classes from the KotlinPoet that have builders don't have
     *  a common interfaces, so we don't have any bounds for [T].
     */
    protected abstract fun initBuilder()

    /**
     * The main internal function to generate a new entity.
     */
    protected abstract fun generateImpl()

    /**
     * The main open function to generate a new entity.
     */
    @Suppress("KDOC_WITHOUT_RETURN_TAG")
    abstract fun generate(): T
}
