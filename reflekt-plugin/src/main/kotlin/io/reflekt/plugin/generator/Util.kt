package io.reflekt.plugin.generator

import com.squareup.kotlinpoet.CodeBlock
import io.reflekt.plugin.analysis.psi.isAnnotatedWith
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import io.reflekt.plugin.generator.GeneratorConstants.qualifiedName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext

fun getInvokedElements(analyzer: ((KtClassOrObject, BindingContext) -> Boolean) -> Set<KtClassOrObject>,
                       filter: (KtClassOrObject, BindingContext) -> Boolean,
                       asSuffix: String) = analyzer(filter).joinToString { "${it.fqName.toString()}$asSuffix" }

// TODO-birillo: rename
fun getWhenBodyForInvokes(fqNameList: Set<String>,
                          analyzer: ((KtClassOrObject, BindingContext) -> Boolean) -> Set<KtClassOrObject>,
                          asSuffix: String): CodeBlock {
    val builder = CodeBlock.builder()
    val filter = { fqName: String -> { clsOrObj: KtClassOrObject, ctx: BindingContext -> (KtClassOrObject::isSubtypeOf)(clsOrObj, setOf(fqName), ctx) } }
    fqNameList.forEach {
        builder.addStatement("%S -> listOf(${getInvokedElements(analyzer, filter(it), asSuffix)})", it)
    }
    return builder.build()
}

// TODO-birillo: rename
fun getWhenBodyForInvokes(fqNamesMap: MutableMap<String, MutableList<String>>,
                          analyzer: ((KtClassOrObject, BindingContext) -> Boolean) -> Set<KtClassOrObject>,
                          asSuffix: String): CodeBlock {
    val builder = CodeBlock.builder()
    fqNamesMap.forEach { (withSubtypeFqName, fqNameList) ->
        val filterSubType = { clsOrObj: KtClassOrObject, ctx: BindingContext -> (KtClassOrObject::isSubtypeOf)(clsOrObj, setOf(withSubtypeFqName), ctx) }
        val filterWithAnnotation = { fqName: String -> { clsOrObj: KtClassOrObject, ctx: BindingContext -> (KtClassOrObject::isAnnotatedWith)(clsOrObj, setOf(fqName), ctx) } }
        val filter = { fqName: String -> { clsOrObj: KtClassOrObject, ctx: BindingContext -> filterWithAnnotation(fqName)(clsOrObj, ctx) && filterSubType(clsOrObj, ctx) } }

        builder
            .beginControlFlow("%S ->", withSubtypeFqName)
            .beginControlFlow("when (%N)", qualifiedName)
        fqNameList.forEach {
            builder.addStatement("%S -> listOf(${getInvokedElements(analyzer, filter(it), asSuffix)})", it)
        }
        builder
            .addStatement("else -> error(%S)", "Unknown $qualifiedName")
            .endControlFlow()
            .endControlFlow()
    }
    return builder.build()
}
