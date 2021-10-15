package io.reflekt

import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.reflect.KClass

@Suppress("unused")
object ReflektImpl {
    class Objects {
        fun <T> withSuperTypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)
        fun <T> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>) =
            WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        class WithSuperTypes<T>(val fqNames: Set<String>) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }

        class WithAnnotations<T>(val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T: Any> withSuperTypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)
        fun <T: Any> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>) =
            WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        class WithSuperTypes<T: Any>(val fqNames: Set<String>) {
            fun toList(): List<KClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }

        class WithAnnotations<T: Any>(val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            fun toList(): List<KClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    class Functions {
        // T - returned class
        class WithAnnotations<T: Function<*>>(val annotationFqNames: Set<String>, signature: String) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }

        fun <T: Function<*>> withAnnotations(annotationFqNames: Set<String>, signature: String) = WithAnnotations<T>(annotationFqNames, signature)
    }

    fun objects() = Objects()
    fun classes() = Classes()
    fun functions() = Functions()
}
