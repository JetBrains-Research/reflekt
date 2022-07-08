package org.jetbrains.reflekt.plugin.compiler

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.reflekt.plugin.compiler.runners.*
import org.jetbrains.reflekt.plugin.compiler.runners.general.AbstractReflektWithLibraryTest
import org.jetbrains.reflekt.plugin.compiler.runners.general.AbstractReflektWithStandaloneProjectTest
import org.jetbrains.reflekt.plugin.util.CodeGenTestPaths

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(
            testDataRoot = CodeGenTestPaths.codeGenResourcesFolder,
            testsRoot = "reflekt-plugin/src/test/java"
        ) {
            testClass<AbstractReflektWithStandaloneProjectTest> {
                model("general-standalone-project-calling")
            }
        }
    }

    generateTestGroupSuiteWithJUnit5 {
        testGroup(
            testDataRoot = CodeGenTestPaths.codeGenResourcesFolder,
            testsRoot = "reflekt-plugin/src/test/java"
        ) {
            testClass<AbstractReflektWithLibraryTest> {
                val excludedTestDataPattern = "^(.+)\\.expected\\.kt?\$"
                model("general-library-calling", excludedPattern=excludedTestDataPattern)
            }
        }
    }
}
