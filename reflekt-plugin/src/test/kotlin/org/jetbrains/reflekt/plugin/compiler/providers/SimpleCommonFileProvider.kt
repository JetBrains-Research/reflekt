package org.jetbrains.reflekt.plugin.compiler.providers

import org.jetbrains.kotlin.test.directives.model.RegisteredDirectives
import org.jetbrains.kotlin.test.model.TestFile
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.AdditionalSourceProvider
import org.jetbrains.kotlin.test.services.TestServices
import java.io.File

class SimpleCommonFileProvider(testServices: TestServices) : AdditionalSourceProvider(testServices) {

    override fun produceAdditionalFiles(globalDirectives: RegisteredDirectives, module: TestModule): List<TestFile> {
//        return listOf(File("reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/common-files/Annotations.kt").toTestFile())
        return listOf(
            File("reflekt-plugin/src/test/resources/org/jetbrains/reflekt/plugin/compiler/common-files/helpers/ReflektCallsCheck.kt").toTestFile(),
        )
    }
}
