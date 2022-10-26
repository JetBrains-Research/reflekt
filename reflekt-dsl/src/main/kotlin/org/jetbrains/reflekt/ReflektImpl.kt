package org.jetbrains.reflekt

@Suppress("UNUSED_PARAMETER")
@InternalReflektApi
public object ReflektImpl {
    /**
     * Returns an instance of [Objects].
     */
    public fun objects(): Objects = Objects()

    /**
     * Returns an instance of [Classes].
     */
    public fun classes(): Classes = Classes()

    /**
     * Returns an instance of [Functions].
     */
    public fun functions(): Functions = Functions()

    public class Objects {
        public fun <T : Any> withSuperTypes(fqNames: Set<String>): WithSuperTypes<T> = WithSuperTypes(fqNames)

        public fun <T : Any> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>): WithAnnotations<T> =
            WithAnnotations(annotationFqNames, supertypeFqNames)

        /**
         * @property fqNames
         */
        @JvmInline
        public value class WithSuperTypes<T : Any>(public val fqNames: Set<String>) {
            public fun toList(): List<ReflektObject<T>> = error("This method should be replaced during compilation")
        }

        /**
         * @property annotationFqNames
         */
        public class WithAnnotations<T : Any>(public val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            public fun toList(): List<ReflektObject<T>> = error("This method should be replaced during compilation")
        }
    }

    public class Classes {
        public fun <T : Any> withSuperTypes(fqNames: Set<String>): WithSuperTypes<T> = WithSuperTypes(fqNames)

        public fun <T : Any> withAnnotations(annotationFqNames: Set<String>, supertypeFqNames: Set<String>): WithAnnotations<T> =
            WithAnnotations(annotationFqNames, supertypeFqNames)

        /**
         * @property fqNames
         */
        @JvmInline
        public value class WithSuperTypes<T : Any>(public val fqNames: Set<String>) {
            public fun toList(): List<ReflektClass<T>> = error("This method should be replaced during compilation")
        }

        /**
         * @property annotationFqNames
         */
        public class WithAnnotations<T : Any>(public val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            public fun toList(): List<ReflektClass<T>> = error("This method should be replaced during compilation")
        }
    }

    public class Functions {
        public fun <T : Function<*>> withAnnotations(annotationFqNames: Set<String>, signature: String): WithAnnotations<T> =
            WithAnnotations(annotationFqNames, signature)

        /**
         * @param T the returned class.
         * @property annotationFqNames
         */
        public class WithAnnotations<out T : Function<*>>(public val annotationFqNames: Set<String>, signature: String) {
            public fun toList(): List<ReflektFunction<T>> = error("This method should be replaced during compilation")
        }
    }
}
