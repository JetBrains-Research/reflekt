package org.jetbrains.reflekt

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction

/**
* The main SmartReflekt DSL.
*/
@Suppress("UNUSED_PARAMETER")
public object SmartReflekt {
    private fun compileTime(): Nothing = error("This method should be replaced during compilation")

    /**
     * The main function for searching classes.
     * The chain of calls has to end with resolve() function.
     *
     * For example:
     * ```
     * SmartReflekt.classes<BInterface>().filter { it.isData() }.resolve()
     * ```
     */
    public fun <T : Any> classes(): ClassCompileTimeExpression<T> = compileTime()

    /**
     * The main function for searching objects.
     * The chain of calls has to end with resolve() function.
     *
     * For example:
     * ```
     * SmartReflekt.objects<BInterface>().filter { it.isCompanion() }.resolve()
     * ```
     */
    public fun <T : Any> objects(): ObjectCompileTimeExpression<T> = compileTime()

    /**
     * The main function for searching functions.
     * The chain of calls has to end with resolve() function.
     *
     * For example:
     * ```
     * SmartReflekt.functions<() -> Unit>().filter { it.isTopLevel && it.name == "foo" }.resolve()
     * ```
     */
    public fun <T : Function<*>> functions(): FunctionCompileTimeExpression<T> = compileTime()

    /**
     * Finds all classes in the project's modules and external libraries (that was marked as libraries to introspect)
     * and filter them by user's condition.
     */
    public class ClassCompileTimeExpression<T : Any> {
        /**
         * Filters classes by user's condition. All classes will be cast to [T] type.
         */
        public fun filter(filter: (IrClass) -> Boolean): ClassCompileTimeExpression<T> = compileTime()

        /**
         * Resolves user's condition - find all classes that satisfy the condition from the filter function.
         */
        public fun resolve(): List<ReflektClass<T>> = compileTime()
    }

    public class ObjectCompileTimeExpression<T : Any> {
        /**
         * Filters objects by user's condition. All objects will be cast to [T] type.
         */
        public fun filter(filter: (IrClass) -> Boolean): ObjectCompileTimeExpression<T> = compileTime()

        /**
         * Resolves user's condition - find all objects that satisfy the condition from the filter function.
         */
        public fun resolve(): List<ReflektObject<T>> = compileTime()
    }

    public class FunctionCompileTimeExpression<T : Function<*>> {
        /**
         * Filters functions by user's condition. All functions will have the same signature.
         */
        public fun filter(filter: (IrFunction) -> Boolean): FunctionCompileTimeExpression<T> = compileTime()

        /**
         * Resolves user's condition - find all functions that satisfy the condition from the filter function.
         */
        public fun resolve(): List<ReflektFunction<T>> = compileTime()
    }
}
