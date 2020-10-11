package io.reflekt

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

object ReflektImpl {
    class Objects {
        fun <T> withSubTypes(fqNames: Set<String>) = WithSubTypes<T>(fqNames)
        fun <T> withAnnotations(annotationFqNames: Set<String>, subtypeFqName: String) = WithAnnotations<T>(annotationFqNames, subtypeFqName)

        class WithSubTypes<T>(val fqNames: Set<String>) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }

        class WithAnnotations<T>(val annotationFqNames: Set<String>, subtypeFqName: String) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T: Any> withSubTypes(fqNames: Set<String>) = WithSubTypes<T>(fqNames)
        fun <T: Any> withAnnotations(annotationFqNames: Set<String>, subtypeFqName: String) = WithAnnotations<T>(annotationFqNames, subtypeFqName)

        class WithSubTypes<T: Any>(val fqNames: Set<String>) {
            fun toList(): List<KClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }

        class WithAnnotations<T: Any>(val annotationFqNames: Set<String>, subtypeFqName: String) {
            fun toList(): List<KClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    class Functions {
        // T - returned class
        class WithAnnotations<T: Any>(private val annotationFqNames: Set<String>) {
            fun toList(): List<KFunction<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<KFunction<T>> = toList().toSet()
        }

        fun <T: Any> withAnnotations(fqNames: Set<String>) = WithAnnotations<T>(fqNames)
    }

    fun objects() = Objects()
    fun classes() = Classes()
    fun functions() = Functions()
}
