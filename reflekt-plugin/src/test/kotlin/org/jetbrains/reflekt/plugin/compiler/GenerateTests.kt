package org.jetbrains.reflekt.plugin.compiler

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.reflekt.plugin.compiler.runners.AbstractBoxTest

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        // TODO: change testDataRoot and testsRoot
        testGroup(
            testDataRoot = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/runners",
            testsRoot = "reflekt-plugin/src/test/java"
        ) {
            testClass<AbstractBoxTest> {
                model("box")
            }
        }
    }
}
