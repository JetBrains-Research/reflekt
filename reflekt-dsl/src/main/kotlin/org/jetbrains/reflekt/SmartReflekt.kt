package org.jetbrains.reflekt

import org.jetbrains.kotlin.ir.declarations.*
import kotlin.reflect.KClass

/*
* The main SmartReflekt DSL for `multi-module` projects
* */
@Suppress("UNUSED_PARAMETER")
object SmartReflekt {
    private fun <T> compileTime(): T = error("This method should be replaced during compilation")

    /*
    * The main function for searching classes. The chain of calls has to end with resolve() function.
    *
    * For example:
    *  SmartReflekt.classes<BInterface>().filter { it.isData() }.resolve()
    * */
    fun <T : Any> classes(): ClassCompileTimeExpression<T> = compileTime()

    /*
    * The main function for searching objects. The chain of calls has to end with resolve() function.
    *
    * For example:
    *  SmartReflekt.objects<BInterface>().filter { it.isCompanion() }.resolve()
    * */
    fun <T : Any> objects(): ObjectCompileTimeExpression<T> = compileTime()

    /*
    * The main function for searching functions. The chain of calls has to end with resolve() function.
    *
    * For example:
    *  SmartReflekt.functions<() -> Unit>().filter { it.isTopLevel && it.name == "foo" }.resolve()
    * */
    fun <T : Function<*>> functions(): FunctionCompileTimeExpression<T> = compileTime()

    /*
    * Find all classes in the project's modules and external libraries (that was marked as libraries to introspect)
    * and filter them by user's condition.
    * */
    class ClassCompileTimeExpression<T : Any> {
        /*
         * Filter classes by user's condition. All classes will be cast to [T] type.
         */
        fun filter(filter: (IrClass) -> Boolean): ClassCompileTimeExpression<T> = compileTime()

        /*
         * Resolve user's condition - find all classes that satisfy the condition from the filter function.
         */
        fun resolve(): List<KClass<T>> = compileTime()
    }

    class ObjectCompileTimeExpression<T : Any> {
        /*
         * Filter objects by user's condition. All objects will be cast to [T] type.
         */
        fun filter(filter: (IrClass) -> Boolean): ObjectCompileTimeExpression<T> = compileTime()

        /*
         * Resolve user's condition - find all objects that satisfy the condition from the filter function.
         */
        fun resolve(): List<T> = compileTime()
    }

    class FunctionCompileTimeExpression<T : Function<*>> {
        /*
         * Filter functions by user's condition. All functions will have the same signature.
         */
        fun filter(filter: (IrFunction) -> Boolean): FunctionCompileTimeExpression<T> = compileTime()

        /*
         * Resolve user's condition - find all functions that satisfy the condition from the filter function.
         */
        fun resolve(): List<T> = compileTime()
    }
}
