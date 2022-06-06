package org.jetbrains.reflekt.plugin.generation.common

/**
 * A class for exception to handle problems with generation (code generation, IR generation)
 *
 * @param message
 * @param cause
 * @property message
 * @property cause
 */
class ReflektGenerationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
