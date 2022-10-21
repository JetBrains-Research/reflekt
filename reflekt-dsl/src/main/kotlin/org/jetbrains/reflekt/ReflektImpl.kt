package org.jetbrains.reflekt

@Suppress("UNUSED_PARAMETER")
@InternalReflektApi
public object ReflektImpl {
    public fun objects(): Objects = Objects()
    public fun classes(): Classes = Classes()
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
            public fun toSet(): Set<ReflektObject<T>> = toList().toSet()
        }

        /**
         * @property annotationFqNames
         */
        public class WithAnnotations<T : Any>(public val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            public fun toList(): List<ReflektObject<T>> = error("This method should be replaced during compilation")
            public fun toSet(): Set<ReflektObject<T>> = toList().toSet()
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
            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()
        }

        /**
         * @property annotationFqNames
         */
        public class WithAnnotations<T : Any>(public val annotationFqNames: Set<String>, supertypeFqNames: Set<String>) {
            public fun toList(): List<ReflektClass<T>> = error("This method should be replaced during compilation")
            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()
        }
    }

    public class Functions {
        public fun <T : Function<*>> withAnnotations(annotationFqNames: Set<String>, signature: String): WithAnnotations<T> = WithAnnotations(annotationFqNames, signature)

        /**
         * @param T returned class
         * @property annotationFqNames
         */
        public class WithAnnotations<out T : Function<*>>(public val annotationFqNames: Set<String>, signature: String) {
            public fun toList(): List<T> = error("This method should be replaced during compilation")
            public fun toSet(): Set<T> = toList().toSet()
        }
    }
}
