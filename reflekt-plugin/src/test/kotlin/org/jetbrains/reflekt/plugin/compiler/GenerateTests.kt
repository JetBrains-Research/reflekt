package org.jetbrains.reflekt.plugin.compiler

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.reflekt.plugin.compiler.runners.AbstractCommonFilesTest
import org.jetbrains.reflekt.plugin.compiler.runners.AbstractHelpersTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(
            testDataRoot = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/code-gen",
            testsRoot = "reflekt-plugin/src/test/java"
        ) {
            testClass<AbstractHelpersTest> {
                model("helpers-test")
            }
        }
    }

    generateTestGroupSuiteWithJUnit5 {
        testGroup(
            testDataRoot = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/code-gen",
            testsRoot = "reflekt-plugin/src/test/java"
        ) {
            testClass<AbstractCommonFilesTest> {
                model("general-calling")
            }
        }
    }
}