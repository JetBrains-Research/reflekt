package io.reflekt.plugin.generator.models

abstract class Generator<T> {
    protected abstract fun initBuilder()
    protected abstract fun generateImpl()
    abstract fun generate(): T
}
