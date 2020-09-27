package io.reflekt.plugin.generator

import io.reflekt.plugin.analysis.psi.isAnnotatedWith
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KFunction2

fun getInvokedElements(fqName: String,
                       analyzer: KFunction2<Array<out String>, (KtClassOrObject, Set<String>, BindingContext) -> Boolean, Set<KtClassOrObject>>,
                       filter: (KtClassOrObject, Set<String>, BindingContext) -> Boolean,
                       asSuffix: String)
    = analyzer(arrayOf(fqName), filter).joinToString { "${it.fqName.toString()}$asSuffix" }

// TODO-birillo: rename, indents
fun getWhenBodyForInvokes(fqNameList: Set<String>,
                          analyzer: KFunction2<Array<out String>, (KtClassOrObject, Set<String>, BindingContext) -> Boolean, Set<KtClassOrObject>>,
                          asSuffix: String): String {
    val builder = StringBuilder()
    //language=kotlin
    builder.append("""
                    ${fqNameList.map{ "\"$it\" -> listOf(${getInvokedElements(it, analyzer, KtClassOrObject::isSubtypeOf, asSuffix)})" }.joinToString(separator = "\n") { it }}
            """)
    return builder.toString()
}

// TODO-birillo: rename, indents
fun getWhenBodyForInvokes(fqNamesMap: MutableMap<String, MutableList<String>>,
                          analyzer: KFunction2<Array<out String>, (KtClassOrObject, Set<String>, BindingContext) -> Boolean, Set<KtClassOrObject>>,
                          asSuffix: String): String {
    val builder = StringBuilder()
    fqNamesMap.forEach{ (withSubtypeFqName, fqNameList) ->
        //language=kotlin
        builder.append("""
                    "$withSubtypeFqName" -> {
                        when (fqName) {
                            ${fqNameList.map{ "\"$it\" -> listOf(${getInvokedElements(it, analyzer, KtClassOrObject::isAnnotatedWith, asSuffix)})" }.joinToString(separator = "\n") { it }}
                            else -> error("Unknown fqName")
                        }
                    }
            """)
    }
    return builder.toString()
}
