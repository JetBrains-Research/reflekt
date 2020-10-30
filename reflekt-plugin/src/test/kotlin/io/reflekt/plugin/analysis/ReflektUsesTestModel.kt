package io.reflekt.plugin.analysis

import org.jetbrains.kotlin.psi.KtNamedFunction

/*
* There are helper data classes to parse JSON.
* TODO: I am not sure that it is the best way, but I did not find a better way.
* */

data class SubtypesToUsesTest(
    val subtypes: Set<String> = emptySet(),
    val uses: MutableList<String> = mutableListOf()
)

data class ClassOrObjectUsesTest(
    val annotations: Set<String> = emptySet(),
    val subtypesToUses: List<SubtypesToUsesTest> = emptyList()
)

data class FunctionUsesTest(
    val annotations: Set<String> = emptySet(),
    val subtypesToUses: MutableList<KtNamedFunction> = mutableListOf()
)


data class ReflektUsesTest(
    val objects: List<ClassOrObjectUsesTest> = emptyList(),
    val classes: List<ClassOrObjectUsesTest> = emptyList(),
    val functions: List<FunctionUsesTest> = emptyList()
) {
    companion object {
        private fun toClassOrObjectUses(items: List<ClassOrObjectUsesTest>) : ClassOrObjectUses {
            val res: ClassOrObjectUses = HashMap()
            items.forEach { (annotations,  subtypesToUses) ->
                val currentMap = mutableMapOf<Set<String>, MutableList<String>>()
                subtypesToUses.forEach { (subtypes, uses) ->
                    currentMap[subtypes] = uses
                }
                res[annotations] = HashMap(currentMap)
            }
            return res
        }

        private fun toFunctionUses(items: List<FunctionUsesTest>) : FunctionUses {
            val res: FunctionUses = HashMap()
            items.forEach { (annotations, subtypesToUses) ->
                res[annotations] = subtypesToUses
            }
            return res
        }
    }

    fun toReflektUses() : ReflektUses = ReflektUses(
        objects = toClassOrObjectUses(this.objects),
        classes = toClassOrObjectUses(this.classes),
        functions = toFunctionUses(this.functions)
    )
}
