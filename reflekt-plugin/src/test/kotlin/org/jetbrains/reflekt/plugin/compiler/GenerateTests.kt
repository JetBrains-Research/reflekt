package org.jetbrains.reflekt.plugin.compiler

import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import org.jetbrains.reflekt.plugin.compiler.runners.*

// check org/jetbrains/kotlin/generators/tests/analysis/api/analysisApi.kt

fun main() {
//    generateTestGroupSuiteWithJUnit5 {
//        testGroup(
//            testDataRoot = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/code-gen/general-calling",
//            testsRoot = "reflekt-plugin/src/test/java"
//        ) {
//            testClass<AbstractFirstExamplesTest> {
//                model("reflekt")
//            }
//        }
//    }

    generateTestGroupSuiteWithJUnit5 {
        testGroup(
            testDataRoot = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/code-gen",
            testsRoot = "reflekt-plugin/src/test/java"
        ) {
            testClass<AbstractTest> {
                model("box")
            }
        }
    }

    generateTestGroupSuiteWithJUnit5 {
        testGroup(
            testDataRoot = "reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/code-gen",
            testsRoot = "reflekt-plugin/src/test/java"
        ) {
            testClass<AbstractSimpleCommonFileTest> {
                model("box")
            }
        }
    }
}
