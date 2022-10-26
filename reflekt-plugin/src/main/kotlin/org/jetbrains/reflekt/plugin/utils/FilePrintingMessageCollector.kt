package org.jetbrains.reflekt.plugin.utils

import org.jetbrains.kotlin.cli.common.messages.*
import java.io.File

class FilePrintingMessageCollector(private val file: File, private val messageRenderer: MessageRenderer, private val verbose: Boolean) :
    MessageCollector {
    constructor(name: String, messageRenderer: MessageRenderer, verbose: Boolean) : this(File(name), messageRenderer, verbose)

    private var hasErrors = false

    init {
        // Clearing the file
        file.writeText("")
    }

    override fun clear() {
        // Do nothing, messages are already reported
    }

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?
    ) {
        if (!verbose && CompilerMessageSeverity.VERBOSE.contains(severity)) return
        hasErrors = hasErrors or severity.isError
        file.appendText(messageRenderer.render(severity, message, location) + System.lineSeparator())
    }

    override fun hasErrors(): Boolean = hasErrors
}
