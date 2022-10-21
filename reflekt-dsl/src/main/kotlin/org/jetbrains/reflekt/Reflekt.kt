package org.jetbrains.reflekt

import org.jetbrains.reflekt.util.stringRepresentation

import kotlin.reflect.KClass
import kotlin.reflect.typeOf

/**
 * The main Reflekt DSL for `multi-module` projects
 */
@Suppress("KDOC_WITHOUT_RETURN_TAG")
@OptIn(InternalReflektApi::class)
public object Reflekt {
    /**
     * The main function for searching objects. The chain of calls has to end with toList() or toSet() function.
     *
     * For example:
     *  Reflekt.objects().withSupertype<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
     *  Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toSet()
     */
    public fun objects(): Objects = Objects()

    /**
     * The main function for searching classes. The chain of calls has to end with toList() or toSet() function.
     *
     * For example:
     *  Reflekt.classes().withSupertype<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
     *  Reflekt.classes().withAnnotations<AInterface>(FirstAnnotation::class).toSet()
     */
    public fun classes(): Classes = Classes()

    /**
     * The main function for searching functions. The chain of calls has to end with toList() or toSet() function.
     *
     * For example:
     *  Reflekt.functions().withAnnotations<() -> Unit>().toList()
     *  Reflekt.functions().withAnnotations<(Int, String) -> List<Int>>().toSet()
     */
    public fun functions(): Functions = Functions()

    /**
     * Find all objects in the project's modules and external libraries (that was marked as libraries to introspect)
     * and filter them by different conditions.
     */
    public class Objects {
        /**
         * Filters objects by one supertype. All objects will be cast to [T] type.
         *
         */
        public inline fun <reified T : Any> withSuperType(): WithSuperTypes<T> = WithSuperTypes(setOf(T::class.qualifiedName!!))

        /**
         * Filters objects by several supertypes. All objects will be cast to [Any] type.
         * If [klasses] was not passed the list\set with result will be empty.
         *
         * @param klasses
         */
        public fun withSuperTypes(vararg klasses: KClass<out Any>): WithSuperTypes<Any> = WithSuperTypes(klasses.mapNotNull { it.qualifiedName }.toSet())

        /**
         * Filters objects by several annotations and supertype [T]. All objects will be cast to [T] type.
         * If [klasses] was not passed the list\set with result will contain only objects with supertype [T].
         *
         * @param klasses
         */
        public inline fun <reified T : Any> withAnnotations(vararg klasses: KClass<out Annotation>): WithAnnotations<T> =
            WithAnnotations(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        /**
         * This class represents DSL for searching objects with several supertypes.
         * Each item in the list\set with result will be cast to [T] type.
         * @property fqNames
         */
        @JvmInline
        public value class WithSuperTypes<T : Any>(public val fqNames: Set<String>) {
            /**
             * Gets the list of objects with [fqNames] supertypes.
             * Each item in the list with result will be cast to [T] type.
             *
             */
            public fun toList(): List<ReflektObject<T>> = ReflektImpl
                .objects()
                .withSuperTypes<T>(fqNames)
                .toList()
                .map { ReflektObject(it) }

            /**
             * Gets set of objects with [fqNames] supertypes.
             * Each item in the set with result will be cast to [T] type.
             */
            public fun toSet(): Set<ReflektObject<T>> = toList().toSet()

            /**
             * Filters objects with [fqNames] supertypes by several annotations.
             * If [klasses] was not passed the list\set with result will contain only objects with [fqNames] supertypes.
             *
             * @param klasses
             */
            public inline fun <reified Q : T> withAnnotations(vararg klasses: KClass<out Annotation>): WithAnnotations<Q> =
                WithAnnotations(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        /**
         * The class represents DSL for searching objects with several annotations.
         * Each item in the list\set with result will be cast to [T] type.
         */
        public class WithAnnotations<T : Any>(private val annotationFqNames: Set<String>, private val supertypeFqNames: Set<String>) {
            /**
             * Get the list of objects with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the list will be cast to [T] type.
             *
             */
            public fun toList(): List<ReflektObject<T>> = ReflektImpl
                .objects()
                .withAnnotations<T>(annotationFqNames, supertypeFqNames)
                .toList()
                .map { ReflektObject(it) }

            /**
             * Gets the set of objects with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the set will be cast to [T] type.
             *
             */
            public fun toSet(): Set<ReflektObject<T>> = toList().toSet()

            /**
             * Filters objects with [annotationFqNames] annotations by one supertype. All objects will be cast to [T] type.
             *
             */
            public inline fun <reified T : Any> withSupertype(): WithSuperTypes<T> = WithSuperTypes(setOf(T::class.qualifiedName!!))

            /**
             * Filters objects with [annotationFqNames] annotations by several supertypes. All objects will be cast to [Any] type.
             *
             * @param klasses
             */
            public fun withSupertypes(vararg klasses: KClass<out Any>): WithSuperTypes<Any> = WithSuperTypes(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    /**
     * Finds all classes in the project's modules and external libraries (that was marked as libraries to introspect)
     * and filter them by different conditions.
     */
    public class Classes {
        /**
         * Filters classes by one supertype. All classes will be cast to [T] type.
         *
         */
        public inline fun <reified T : Any> withSuperType(): WithSuperTypes<T> = WithSuperTypes(setOf(T::class.qualifiedName!!))

        /**
         * Filters classes by several supertypes. All classes will be cast to [Any] type.
         * If [klasses] was not passed the list\set with result will be empty.
         *
         * @param klasses
         */
        public fun withSuperTypes(vararg klasses: KClass<out Any>): WithSuperTypes<Any> = WithSuperTypes(klasses.mapNotNull { it.qualifiedName }.toSet())

        /**
         * Filters classes by several annotations and supertype [T]. All classes will be cast to [T] type.
         * If [klasses] was not passed the list\set with result will contain only classes with supertype [T].
         *
         * @param klasses
         */
        public inline fun <reified T : Any> withAnnotations(vararg klasses: KClass<out Annotation>): WithAnnotations<T> =
            WithAnnotations(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        /**
         * The class represents DSL for searching classes with several supertypes.
         * Each item in the list\set with result will be cast to [T] type.
         * @property fqNames
         */
        @JvmInline
        public value class WithSuperTypes<T : Any>(public val fqNames: Set<String>) {
            /**
             * Get list of classes with [fqNames] supertypes.
             * Each item in the list\set with result will be cast to [T] type.
             *
             */
            public fun toList(): List<ReflektClass<T>> = ReflektImpl.classes().withSuperTypes<T>(fqNames).toList()

            /**
             * Get set of classes with [fqNames] supertypes.
             * Each item in the list\set with result will be cast to [T] type.
             *
             */
            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()

            /**
             * Filters classes with [fqNames] supertypes by several annotations.
             * If [klasses] was not passed the list\set with result will contain only classes with [fqNames] supertypes.
             *
             * @param klasses
             */
            public inline fun <reified Q : T> withAnnotations(vararg klasses: KClass<out Annotation>): WithAnnotations<Q> =
                WithAnnotations(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        /**
         * The class represents DSL for searching classes with several annotations.
         * Each item in the list\set with result will be cast to [T] type.
         * @property supertypeFqNames
         */
        public class WithAnnotations<T : Any>(private val annotationFqNames: Set<String>, public val supertypeFqNames: Set<String>) {
            /**
             * Gets the list of classes with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the list or set with result will be cast to [T] type.
             *
             */
            public fun toList(): List<ReflektClass<T>> = ReflektImpl.classes().withAnnotations<T>(annotationFqNames, supertypeFqNames).toList()

            /**
             * Gets set of classes with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the list\set with result will be cast to [T] type.
             *
             */
            public fun toSet(): Set<ReflektClass<T>> = toList().toSet()

            /**
             * Filters classes with [annotationFqNames] annotations by one supertype. All classes will be cast to [T] type.
             */
            public inline fun <reified T : Any> withSupertype(): WithSuperTypes<T> = WithSuperTypes(supertypeFqNames)

            /**
             * Filters classes with [annotationFqNames] annotations by several supertypes. All classes will be cast to [Any] type.
             *
             * @param klasses
             */
            public fun withSupertypes(vararg klasses: KClass<out Any>): WithSuperTypes<Any> = WithSuperTypes(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    /**
     * Find all functions in the project's modules and external libraries (that was marked as libraries to introspect)
     * and filter them by different conditions.
     */
    public class Functions {
        /**
         * Filters functions with [T] signature by several annotations.
         * If [klasses] was not passed the list\set with result will contain only functions with [T] signature.
         *
         * @param klasses
         */
        public inline fun <reified T : Function<*>> withAnnotations(vararg klasses: KClass<out Annotation>): WithAnnotations<T> =
            WithAnnotations(klasses.mapNotNull { it.qualifiedName }.toSet(), typeOf<T>().stringRepresentation())

        /**
         * The class represents DSL for searching functions by the signature with several annotations.
         */
        public class WithAnnotations<out T : Function<*>>(private val annotationFqNames: Set<String>, private val signature: String) {
            /**
             * Gets the list of functions with [T] signature and [annotationFqNames] annotations.
             *
             */
            public fun toList(): List<T> = ReflektImpl.functions().withAnnotations<T>(annotationFqNames, signature).toList()

            /**
             * Gets set of functions with [T] signature and [annotationFqNames] annotations.
             *
             */
            public fun toSet(): Set<T> = toList().toSet()
        }
    }
}
