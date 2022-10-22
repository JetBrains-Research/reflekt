package org.jetbrains.reflekt

/**
 * Represents a callable entity, such as a function or a property.
 */
public interface ReflektCallable : ReflektAnnotatedElement {
    /**
     * The name of this callable as it was declared in the source code.
     * If the callable has no name, a special invented name is created.
     * Nameless callables include:
     * - constructors have the name "<init>",
     * - property accessors: the getter for a property named "foo" will have the name "<get-foo>",
     *   the setter, similarly, will have the name "<set-foo>".
     */
    public val name: String

    /**
     * Visibility of this callable, or `null` if its visibility cannot be represented in Kotlin.
     */
    public val visibility: ReflektVisibility?

    /**
     * `true` if this callable is `final`.
     */
    public val isFinal: Boolean

    /**
     * `true` if this callable is `open`.
     */
    public val isOpen: Boolean

    /**
     * `true` if this callable is `abstract`.
     */
    public val isAbstract: Boolean

    /**
     * `true` if this is a suspending function.
     */
    public val isSuspend: Boolean
}
