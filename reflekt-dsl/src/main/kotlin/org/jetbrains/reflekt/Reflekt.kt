package org.jetbrains.reflekt

import org.jetbrains.reflekt.util.stringRepresentation
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

/**
 * The main Reflekt DSL for `multi-module` projects
 */
object Reflekt {
    /**
     * Find all objects in the project's modules and external libraries (that was marked as libraries to introspect)
     * and filter them by different conditions.
     */
    class Objects {
        /**
         * Filter objects by one supertype. All objects will be cast to [T] type.
         */
        inline fun <reified T : Any> withSupertype() = WithSupertypes<T>(setOf(T::class.qualifiedName!!))

        /**
         * Filter objects by several supertypes. All objects will be cast to [Any] type.
         * If [klasses] was not passed the list\set with result will be empty.
         */
        fun withSupertypes(vararg klasses: KClass<out Any>) = WithSupertypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())


        /**
         * Filter objects by several annotations and supertype [T]. All objects will be cast to [T] type.
         * If [klasses] was not passed the list\set with result will contain only objects with supertype [T].
         */
        inline fun <reified T : Any> withAnnotations(vararg klasses: KClass<out Annotation>) =
            WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        /**
         * The class represents DSL for searching objects with several supertypes.
         * Each item in the list\set with result will be cast to [T] type.
         */
        class WithSupertypes<T : Any>(val fqNames: Set<String>) {
            /**
             * Get list of objects with [fqNames] supertypes.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toList(): List<T> = ReflektImpl.objects().withSupertypes<T>(fqNames).toList()

            /**
             * Get set of objects with [fqNames] supertypes.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toSet(): Set<T> = toList().toSet()


            /**
             * Filter objects with [fqNames] supertypes by several annotations.
             * If [klasses] was not passed the list\set with result will contain only objects with [fqNames] supertypes.
             */
            inline fun <reified Q : T> withAnnotations(vararg klasses: KClass<out Annotation>) =
                WithAnnotations<Q>(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        /**
         * The class represents DSL for searching objects with several annotations.
         * Each item in the list\set with result will be cast to [T] type.
         */
        class WithAnnotations<T : Any>(private val annotationFqNames: Set<String>, private val supertypeFqNames: Set<String>) {
            /**
             * Get list of objects with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toList(): List<T> = ReflektImpl.objects().withAnnotations<T>(annotationFqNames, supertypeFqNames).toList()

            /**
             * Get set of objects with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toSet(): Set<T> = toList().toSet()


            /**
             * Filter objects with [annotationFqNames] annotations by one supertype. All objects will be cast to [T] type.
             */
            inline fun <reified T : Any> withSupertype() = WithSupertypes<T>(setOf(T::class.qualifiedName!!))

            /**
             * Filter objects with [annotationFqNames] annotations by several supertypes. All objects will be cast to [Any] type.
             */
            fun withSupertypes(vararg klasses: KClass<out Any>) = WithSupertypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    /**
     * Find all classes in the project's modules and external libraries (that was marked as libraries to introspect)
     * and filter them by different conditions.
     */
    class Classes {
        /**
         * Filter classes by one supertype. All classes will be cast to [T] type.
         */
        inline fun <reified T : Any> withSupertype() = WithSupertypes<T>(setOf(T::class.qualifiedName!!))

        /**
         * Filter classes by several supertypes. All classes will be cast to [Any] type.
         * If [klasses] was not passed the list\set with result will be empty.
         */
        fun withSupertypes(vararg klasses: KClass<out Any>) = WithSupertypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())


        /**
         * Filter classes by several annotations and supertype [T]. All classes will be cast to [T] type.
         * If [klasses] was not passed the list\set with result will contain only classes with supertype [T].
         */
        inline fun <reified T : Any> withAnnotations(vararg klasses: KClass<out Annotation>) =
            WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        /**
         * The class represents DSL for searching classes with several supertypes.
         * Each item in the list\set with result will be cast to [T] type.
         */
        class WithSupertypes<T : Any>(val fqNames: Set<String>) {
            /**
             * Get list of classes with [fqNames] supertypes.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withSupertypes<T>(fqNames).toList()

            /**
             * Get set of classes with [fqNames] supertypes.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toSet(): Set<KClass<T>> = toList().toSet()


            /**
             * Filter classes with [fqNames] supertypes by several annotations.
             * If [klasses] was not passed the list\set with result will contain only classes with [fqNames] supertypes.
             */
            inline fun <reified Q : T> withAnnotations(vararg klasses: KClass<out Annotation>) =
                WithAnnotations<Q>(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        /**
         * The class represents DSL for searching classes with several annotations.
         * Each item in the list\set with result will be cast to [T] type.
         */
        class WithAnnotations<T : Any>(private val annotationFqNames: Set<String>, val supertypeFqNames: Set<String>) {
            /**
             * Get list of classes with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withAnnotations<T>(annotationFqNames, supertypeFqNames).toList()

            /**
             * Get set of classes with [supertypeFqNames] supertypes and [annotationFqNames] annotations.
             * Each item in the list\set with result will be cast to [T] type.
             */
            fun toSet(): Set<KClass<T>> = toList().toSet()


            /**
             * Filter classes with [annotationFqNames] annotations by one supertype. All classes will be cast to [T] type.
             */
            inline fun <reified T : Any> withSupertype() = WithSupertypes<T>(supertypeFqNames)

            /**
             * Filter classes with [annotationFqNames] annotations by several supertypes. All classes will be cast to [Any] type.
             */
            fun withSupertypes(vararg klasses: KClass<out Any>) = WithSupertypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    /**
     * Find all functions in the project's modules and external libraries (that was marked as libraries to introspect)
     * and filter them by different conditions.
     */
    class Functions {
        /**
         * The class represents DSL for searching functions by the signature with several annotations.
         */
        class WithAnnotations<T : Function<*>>(private val annotationFqNames: Set<String>, private val signature: String) {
            /**
             * Get list of functions with [T] signature and [annotationFqNames] annotations.
             */
            fun toList(): List<T> = ReflektImpl.functions().withAnnotations<T>(annotationFqNames, signature).toList()

            /**
             * Get set of functions with [T] signature and [annotationFqNames] annotations.
             */
            fun toSet(): Set<T> = toList().toSet()
        }


        /**
         * Filter functions with [T] signature by several annotations.
         * If [klasses] was not passed the list\set with result will contain only functions with [T] signature.
         */
        @OptIn(ExperimentalStdlibApi::class)
        inline fun <reified T : Function<*>> withAnnotations(vararg klasses: KClass<out Annotation>) =
            WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), typeOf<T>().stringRepresentation())
    }

    /**
     * The main function for searching objects. The chain of calls has to end with toList() or toSet() function.
     *
     * For example:
     *  Reflekt.objects().withSupertype<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
     *  Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toSet()
     */
    fun objects() = Objects()

    /**
     * The main function for searching classes. The chain of calls has to end with toList() or toSet() function.
     *
     * For example:
     *  Reflekt.classes().withSupertype<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
     *  Reflekt.classes().withAnnotations<AInterface>(FirstAnnotation::class).toSet()
     */
    fun classes() = Classes()

    /**
     * The main function for searching functions. The chain of calls has to end with toList() or toSet() function.
     *
     * For example:
     *  Reflekt.functions().withAnnotations<() -> Unit>().toList()
     *  Reflekt.functions().withAnnotations<(Int, String) -> List<Int>>().toSet()
     */
    fun functions() = Functions()
}
