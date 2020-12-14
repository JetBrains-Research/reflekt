package io.reflekt.plugin.generation.code.generator.models

abstract class Generator<T> {
    protected abstract fun initBuilder()
    protected abstract fun generateImpl()
    abstract fun generate(): T
}
