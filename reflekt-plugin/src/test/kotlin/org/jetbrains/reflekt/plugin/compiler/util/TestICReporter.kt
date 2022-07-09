package org.jetbrains.reflekt.plugin.compiler.util

import org.jetbrains.kotlin.build.report.ICReporterBase
import org.jetbrains.kotlin.cli.common.ExitCode
import java.io.File

class TestICReporter(private val isVerbose: Boolean = false) : ICReporterBase() {
    var exitCode: ExitCode = ExitCode.OK
        private set

    val icLogLines = arrayListOf<String>()

    override fun report(message: () -> String) {
        icLogLines.add(message())
    }

    override fun reportVerbose(message: () -> String) {
        if (isVerbose) {
            report(message)
        }
    }

    override fun reportCompileIteration(incremental: Boolean, sourceFiles: Collection<File>, exitCode: ExitCode) {
        this.exitCode = exitCode
    }
}
