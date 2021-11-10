package io.reflekt.plugin.generation.common

class ReflektGenerationException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception(message, cause)
