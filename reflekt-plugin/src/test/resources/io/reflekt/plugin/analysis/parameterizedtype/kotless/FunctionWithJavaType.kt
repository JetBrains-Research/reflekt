package io.reflekt.plugin.analysis.parameterizedtype.kotless

/**
 * @kotlinType Function1<List<List<Any>>, Unit> (kotlin.Function1)
 * @subtypes:
 *   [foo1_Any_Unit],
 *   [foo1_ListAny_Unit]
 */
fun <T> Sequence<T>.reversed() = this.toList().asReversed().asSequence()
