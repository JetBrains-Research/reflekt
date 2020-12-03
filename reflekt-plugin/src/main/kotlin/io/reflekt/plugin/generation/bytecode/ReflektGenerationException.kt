package io.reflekt.plugin.generation.bytecode

class ReflektGenerationException(
    override val message: String? = null,
    override val cause: Throwable? = null
): Exception(message, cause)
