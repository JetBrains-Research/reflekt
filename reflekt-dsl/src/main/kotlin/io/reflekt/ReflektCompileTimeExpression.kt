package io.reflekt

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.reflekt.models.compileTime
import kotlin.reflect.KClass

/**
 * Use Kotlin Symbol Processing API (https://github.com/google/ksp) for classes, objects, and functions
 */
class ReflektCompileTimeExpression {
    class ClassCompileTimeExpression(val classes: Set<KSClassDeclaration>) {
        fun filter(filter: (KSClassDeclaration) -> Boolean): ClassCompileTimeExpression = compileTime()
        fun <T : Any> resolve(): List<KClass<T>> = compileTime()
    }

    fun classes(): ClassCompileTimeExpression = compileTime()

    class ObjectCompileTimeExpression(val objects: Set<KSClassDeclaration>) {
        fun filter(filter: (KSClassDeclaration) -> Boolean): ObjectCompileTimeExpression = compileTime()
        fun <T : Any> resolve(): List<T> = compileTime()
    }

    fun objects(): ObjectCompileTimeExpression = compileTime()

    class FunctionCompileTimeExpression(val functions: Set<KSFunctionDeclaration>) {
        fun filter(filter: (KSFunctionDeclaration) -> Boolean): ObjectCompileTimeExpression = compileTime()
        fun <T : Function<*>> resolve(): List<T> = compileTime()
    }

    fun functions(): FunctionCompileTimeExpression = compileTime()
}
