package org.jetbrains.reflekt

public interface ReflektFunction<out T : Function<*>> : ReflektCallable {
    /**
     * Actual function instance.
     */
    public val function: T

    /**
     * `true` if this function is `inline`.
     * See the [Kotlin language documentation](https://kotlinlang.org/docs/reference/inline-functions.html)
     * for more information.
     */
    public val isInline: Boolean

    /**
     * `true` if this function is `external`.
     * See the [Kotlin language documentation](https://kotlinlang.org/docs/reference/java-interop.html#using-jni-with-kotlin)
     * for more information.
     */
    public val isExternal: Boolean

    /**
     * `true` if this function is `operator`.
     * See the [Kotlin language documentation](https://kotlinlang.org/docs/reference/operator-overloading.html)
     * for more information.
     */
    public val isOperator: Boolean

    /**
     * `true` if this function is `infix`.
     * See the [Kotlin language documentation](https://kotlinlang.org/docs/reference/functions.html#infix-notation)
     * for more information.
     */
    public val isInfix: Boolean

    /**
     * `true` if this is a suspending function.
     */
    public override val isSuspend: Boolean
}

public data class ReflektFunctionImpl<out T : Function<*>>(
    override val function: T,
    override val annotations: Set<Annotation>,
    override val name: String,
    override val visibility: ReflektVisibility?,
    override val isFinal: Boolean,
    override val isOpen: Boolean,
    override val isAbstract: Boolean,
    override val isInline: Boolean,
    override val isExternal: Boolean,
    override val isOperator: Boolean,
    override val isInfix: Boolean,
    override val isSuspend: Boolean,
) : ReflektFunction<T>
