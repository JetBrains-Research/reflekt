package org.jetbrains.reflekt

/**
 * Visibility is an aspect of a Kotlin declaration regulating where that declaration is accessible in the source code.
 * Visibility can be changed with one of the following modifiers: `public`, `protected`, `internal`, `private`.
 */
public enum class ReflektVisibility {
    /**
     * Visibility of declarations marked with the `public` modifier, or with no modifier at all.
     */
    PUBLIC,

    /**
     * Visibility of declarations marked with the `protected` modifier.
     */
    PROTECTED,

    /**
     * Visibility of declarations marked with the `internal` modifier.
     */
    INTERNAL,

    /**
     * Visibility of declarations marked with the `private` modifier.
     */
    PRIVATE,
    ;
}
