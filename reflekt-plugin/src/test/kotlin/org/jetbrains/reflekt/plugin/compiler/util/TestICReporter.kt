package org.jetbrains.reflekt.plugin.compiler.util

import org.jetbrains.kotlin.build.report.ICReporter
import org.jetbrains.kotlin.build.report.ICReporterBase
import org.jetbrains.kotlin.cli.common.ExitCode
import java.io.File

class TestICReporter(private val isVerbose: Boolean = false) : ICReporterBase() {
    var exitCode: ExitCode = ExitCode.OK
        private set

    val icLogLines = ArrayList<String>()

    override fun report(message: () -> String, severity: ICReporter.ReportSeverity) {
        if (isVerbose || severity.level >= ICReporter.ReportSeverity.INFO.level) {
            icLogLines.add(message())
        }
    }

    override fun reportCompileIteration(incremental: Boolean, sourceFiles: Collection<File>, exitCode: ExitCode) {
        this.exitCode = exitCode
    }
}
