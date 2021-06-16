package io.reflekt

import io.reflekt.models.compileTime
import kotlin.reflect.KClass

@Suppress("UNUSED_PARAMETER")
object ReflektImpl {
    class Objects {
        fun <T> withSubTypes(fqNames: Set<String>) = WithSubTypes<T>(fqNames)
        fun <T> withAnnotations(annotationFqNames: Set<String>, subtypeFqNames: Set<String>) =
            WithAnnotations<T>(annotationFqNames, subtypeFqNames)

        class WithSubTypes<T>(val fqNames: Set<String>) {
            fun toList(): List<T> = compileTime()
            fun toSet(): Set<T> = toList().toSet()
        }

        class WithAnnotations<T>(val annotationFqNames: Set<String>, subtypeFqNames: Set<String>) {
            fun toList(): List<T> = compileTime()
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T: Any> withSubTypes(fqNames: Set<String>) = WithSubTypes<T>(fqNames)
        fun <T: Any> withAnnotations(annotationFqNames: Set<String>, subtypeFqNames: Set<String>) =
            WithAnnotations<T>(annotationFqNames, subtypeFqNames)

        class WithSubTypes<T: Any>(val fqNames: Set<String>) {
            fun toList(): List<KClass<T>> = compileTime()
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }

        class WithAnnotations<T: Any>(val annotationFqNames: Set<String>, subtypeFqNames: Set<String>) {
            fun toList(): List<KClass<T>> = compileTime()
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    class Functions {
        // T - returned class
        class WithAnnotations<T: Function<*>>(val annotationFqNames: Set<String>) {
            fun toList(): List<T> = compileTime()
            fun toSet(): Set<T> = toList().toSet()
        }

        fun <T: Function<*>> withAnnotations(fqNames: Set<String>) = WithAnnotations<T>(fqNames)
    }

    fun objects() = Objects()
    fun classes() = Classes()
    fun functions() = Functions()
}
