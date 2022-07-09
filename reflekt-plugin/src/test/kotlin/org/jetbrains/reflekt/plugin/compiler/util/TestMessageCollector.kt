package org.jetbrains.reflekt.plugin.compiler.util

import org.jetbrains.kotlin.cli.common.messages.*

class TestMessageCollector : MessageCollector {
    val errors = ArrayList<String>()

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity.isError) {
            errors.add(message)
        }
    }

    override fun clear() {
        errors.clear()
    }

    override fun hasErrors(): Boolean =
        errors.isNotEmpty()
}
