package org.jetbrains.reflekt

@Suppress("UNUSED_PARAMETER")
object ReflektImpl {
    fun objects() = Objects()
    fun classes() = Classes()
    fun functions() = Functions()
    class Objects {
        fun <T> withSuperTypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)
        fun <T> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>) =
            WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        /**
         * @property fqNames
         */
        @JvmInline
        value class WithSuperTypes<out T>(val fqNames: Set<String>) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }

        /**
         * @property annotationFqNames
         */
        class WithAnnotations<out T>(val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T : Any> withSuperTypes(fqNames: Set<String>) = WithSuperTypes<T>(fqNames)
        fun <T : Any> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>) =
            WithAnnotations<T>(annotationFqNames, supertypeFqNames)

        /**
         * @property fqNames
         */
        @JvmInline
        value class WithSuperTypes<T : Any>(val fqNames: Set<String>) {
            fun toList(): List<ReflektClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<ReflektClass<T>> = toList().toSet()
        }

        /**
         * @property annotationFqNames
         */
        class WithAnnotations<T : Any>(val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            fun toList(): List<ReflektClass<T>> = error("This method should be replaced during compilation")
            fun toSet(): Set<ReflektClass<T>> = toList().toSet()
        }
    }

    class Functions {
        fun <T : Function<*>> withAnnotations(annotationFqNames: Set<String>, signature: String) = WithAnnotations<T>(annotationFqNames, signature)

        /**
         * @param T returned class
         * @property annotationFqNames
         */
        class WithAnnotations<out T : Function<*>>(val annotationFqNames: Set<String>, signature: String) {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }
}
