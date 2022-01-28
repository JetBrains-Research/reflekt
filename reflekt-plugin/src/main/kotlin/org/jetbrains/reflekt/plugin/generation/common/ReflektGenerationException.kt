package org.jetbrains.reflekt.plugin.generation.common

/**
 * Class for exception because of problem with generation (code generation, IR generation)
 *
 * @param message
 * @param cause
 * @property message
 * @property cause
 */
class ReflektGenerationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception(message, cause)
