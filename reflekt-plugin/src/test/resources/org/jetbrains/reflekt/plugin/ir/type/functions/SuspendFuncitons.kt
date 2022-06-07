@file:Suppress("RedundantSuspendModifier")

package org.jetbrains.reflekt.plugin.ir.type.functions

/**
 * @kotlinType Function0<Unit> (kotlin.Function0)
 * @subtypes:
 */
suspend fun suspend_foo0_Unit() {}

/**
 * @kotlinType Function0<Any> (kotlin.Function0)
 * @subtypes:
 *  [suspend_foo0_Unit]
 */
suspend fun suspend_foo_Any(): Any { TODO() }



