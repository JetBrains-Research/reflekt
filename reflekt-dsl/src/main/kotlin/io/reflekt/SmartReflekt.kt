package io.reflekt

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.reflekt.models.compileTime
import kotlin.reflect.KClass

/**
 * Use Kotlin Symbol Processing API (https://github.com/google/ksp) for classes, objects, and functions
 */
object SmartReflekt {
    class ClassCompileTimeExpression<T : Any>(private val classes: Set<KSClassDeclaration>) {
        fun filter(filter: (KSClassDeclaration) -> Boolean): ClassCompileTimeExpression<T> = compileTime()
        fun resolve(): List<KClass<T>> = compileTime()
    }

    fun <T : Any> classes(): ClassCompileTimeExpression<T> = compileTime()

    class ObjectCompileTimeExpression<T : Any>(private val objects: Set<KSClassDeclaration>) {
        fun filter(filter: (KSClassDeclaration) -> Boolean): ObjectCompileTimeExpression<T> = compileTime()
        fun resolve(): List<T> = compileTime()
    }

    fun <T : Any> objects(): ObjectCompileTimeExpression<T> = compileTime()

    class FunctionCompileTimeExpression<T : Function<*>>(private val functions: Set<KSFunctionDeclaration>) {
        fun filter(filter: (KSFunctionDeclaration) -> Boolean): FunctionCompileTimeExpression<T> = compileTime()
        fun resolve(): List<T> = compileTime()
    }

    fun <T : Function<*>> functions(): FunctionCompileTimeExpression<T> = compileTime()
}
