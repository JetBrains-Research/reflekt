package org.jetbrains.reflekt.plugin.compiler.directives

import org.jetbrains.kotlin.test.directives.model.SimpleDirectivesContainer

object ReflektPluginCallDirectives : SimpleDirectivesContainer() {
    val DUMP_REFLEKT_IMPL by directive("Dump generated ReflektImpl file")
}
