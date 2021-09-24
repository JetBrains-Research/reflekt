package io.reflekt.plugin.ic

import org.jetbrains.kotlin.build.report.ICReporterBase
import org.jetbrains.kotlin.cli.common.ExitCode
import java.io.File

class TestICReporter : ICReporterBase() {

    var exitCode: ExitCode = ExitCode.OK
        private set

    override fun report(message: () -> String) {
    }

    override fun reportVerbose(message: () -> String) {
    }

    override fun reportCompileIteration(incremental: Boolean, sourceFiles: Collection<File>, exitCode: ExitCode) {
        this.exitCode = exitCode
    }
}
