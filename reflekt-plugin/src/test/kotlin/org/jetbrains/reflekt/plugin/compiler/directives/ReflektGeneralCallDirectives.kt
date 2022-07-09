package org.jetbrains.reflekt.plugin.compiler.directives

import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer

object ReflektGeneralCallDirectives : SimpleDirectivesContainer() {
    val DUMP_REFLEKT_IMPL by directive("Make a dump for the generated ReflektImpl file")

    val COMPILE_REFLEKT_IMPL by directive("Compile the generated ReflektImpl file")
}
