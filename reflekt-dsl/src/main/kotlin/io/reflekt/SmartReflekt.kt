package io.reflekt

import io.reflekt.models.compileTime
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import kotlin.reflect.KClass

@Suppress("UNUSED_PARAMETER")
object SmartReflekt {
    class ClassCompileTimeExpression<T : Any> {
        fun filter(filter: (KtClass) -> Boolean): ClassCompileTimeExpression<T> = compileTime()
        fun resolve(): List<KClass<T>> = compileTime()
    }

    fun <T : Any> classes(): ClassCompileTimeExpression<T> = compileTime()

    class ObjectCompileTimeExpression<T : Any> {
        fun filter(filter: (KtObjectDeclaration) -> Boolean): ObjectCompileTimeExpression<T> = compileTime()
        fun resolve(): List<T> = compileTime()
    }

    fun <T : Any> objects(): ObjectCompileTimeExpression<T> = compileTime()

    class FunctionCompileTimeExpression<T : Function<*>> {
        fun filter(filter: (KtNamedFunction) -> Boolean): FunctionCompileTimeExpression<T> = compileTime()
        fun resolve(): List<T> = compileTime()
    }

    fun <T : Function<*>> functions(): FunctionCompileTimeExpression<T> = compileTime()
}
