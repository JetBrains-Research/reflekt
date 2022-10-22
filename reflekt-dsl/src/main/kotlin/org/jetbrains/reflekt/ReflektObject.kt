package org.jetbrains.reflekt

/**
 * Represents a [ReflektClass], which definitely describes an `object` declaration.
 *
 * @param reflektClass The delegated [ReflektClass], [ReflektClass.objectInstance] of it must not be `null`.
 */
@JvmInline
public value class ReflektObject<T : Any> @InternalReflektApi constructor(private val reflektClass: ReflektClass<T>) : ReflektClass<T> by reflektClass {
    override val objectInstance: T
        get() = checkNotNull(reflektClass.objectInstance) { "Object instance of $reflektClass isn't available" }

    init {
        requireNotNull(reflektClass.objectInstance) { "There must be an object instance in $reflektClass" }
    }

    override fun toString(): String = reflektClass.toString()
}
