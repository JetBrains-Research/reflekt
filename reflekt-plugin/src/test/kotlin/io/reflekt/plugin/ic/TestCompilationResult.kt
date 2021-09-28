package io.reflekt.plugin.ic

import org.jetbrains.kotlin.cli.common.ExitCode

data class TestCompilationResult(
    val exitCode: ExitCode,
    val compileErrors: Collection<String>
) {
    constructor(
        icReporter: TestICReporter,
        messageCollector: TestMessageCollector
    ) : this(icReporter.exitCode, messageCollector.errors)
}
