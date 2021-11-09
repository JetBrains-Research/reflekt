package io.reflekt

import kotlin.reflect.KClass

@Suppress("unused")
object ReflektImpl {
    fun objects() = Objects()
    fun classes() = Classes()
    fun functions() = Functions()
    class Objects {
        fun <T> withSupertypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)
        fun <T> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>) =
                WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        /**
         * @property fqNames
         */
        @JvmInline
        value class WithSuperTypes<T>(val fqNames: Set<String>) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }

        /**
         * @property annotationFqNames
         */
        class WithAnnotations<T>(val annotationFqNames: Set<String>, @Suppress("UNUSED_PARAMETER") supertypeFqNames: Set<String>) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T : Any> withSupertypes(fqNames: Set<String>) = WithSupertypes<T>(fqNames)
        fun <T : Any> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>) =
                WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        /**
         * @property fqNames
         */
        @JvmInline
        value class WithSupertypes<T : Any>(val fqNames: Set<String>) {
            fun toList(): List<KClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }

        /**
         * @property annotationFqNames
         */
        class WithAnnotations<T : Any>(val annotationFqNames: Set<String>, @Suppress("UNUSED_PARAMETER") supertypeFqNames: Set<String>) {
            fun toList(): List<KClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    class Functions {
        fun <T : Function<*>> withAnnotations(annotationFqNames: Set<String>, signature: String) = WithAnnotations<T>(annotationFqNames, signature)

        /**
         * @property annotationFqNames
         */
        // T - returned class
        class WithAnnotations<T : Function<*>>(val annotationFqNames: Set<String>, @Suppress("UNUSED_PARAMETER") signature: String) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }
}
