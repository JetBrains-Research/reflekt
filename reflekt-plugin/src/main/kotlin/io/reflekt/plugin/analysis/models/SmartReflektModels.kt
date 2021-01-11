package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.instances.*
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

/*
 * Store a set of qualified names that exist in the project and additional libraries
 */
data class ReflektInstances(
    val objects: List<KtClassOrObject> = ArrayList(),
    val classes: List<KtClassOrObject> = ArrayList(),
    val functions: List<KtNamedFunction> = ArrayList()
) {
    companion object{
        fun createByProcessors(processors: Set<BaseInstancesProcessor<*>>) = ReflektInstances(
            objects = processors.mapNotNull { it as? ObjectInstancesProcessor }.first().instances,
            classes = processors.mapNotNull { it as? ClassInstancesProcessor }.first().instances,
            functions = processors.mapNotNull { it as? FunctionInstancesProcessor }.first().instances
        )
    }
}

data class SubTypesToFilters(
    val subType: String? = null,
    val filters: Set<Lambda> = emptySet()
)

data class Lambda(
    val body: String,
    val parameters: List<String> = listOf("it")
)
