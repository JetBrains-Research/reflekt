package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.instances.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/*
 * Store a set of qualified names that exist in the project and additional libraries
 */
data class ReflektInstances(
    val objects: List<KtObjectDeclaration> = ArrayList(),
    val classes: List<KtClass> = ArrayList(),
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
    val subType: ParameterizedType? = null,
    val filters: List<Lambda> = emptyList(),
    val imports: List<Import> = emptyList()
)

data class Lambda(
    val body: String,
    val parameters: List<String> = listOf("it")
)

data class Import(
    val fqName: String,
    val text: String
)
