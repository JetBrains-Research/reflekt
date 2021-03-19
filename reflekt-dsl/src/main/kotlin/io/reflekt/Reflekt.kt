package io.reflekt

import kotlin.reflect.KClass

/*
* The main Reflekt DSL for `multi-module` projects
* */
object Reflekt {
    /*
    * Find all objects in the project's modules and external libraries (that was marked as libraries to introspect)
    * and filter them by different conditions.
    * */
    class Objects {
        /*
         * Filter objects by one subtype. All objects will be cast to [T] type.
         */
        inline fun <reified T: Any> withSubType() = WithSubTypes<T>(setOf(T::class.qualifiedName!!))
        /*
         * Filter objects by several subtypes. All objects will be cast to [Any] type.
         * If [klasses] was not passed the list\set with result will be empty.
         */
        fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())


        /*
         * Filter objects by several annotations and subtype [T]. All objects will be cast to [T] type.
         * If [klasses] was not passed the list\set with result will contain only objects with subtype [T].
         */
        inline fun <reified T: Any> withAnnotations(vararg klasses: KClass<out Annotation>) =
            WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        /*
        * The class represents DSL for searching objects with several subtypes.
        * Each item in the list\set with result will be casted to [T] type.
        * */
        class WithSubTypes<T: Any>(val fqNames: Set<String>) {
            /*
            * Get list of objects with [fqNames] subtypes.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toList(): List<T> = ReflektImpl.objects().withSubTypes<T>(fqNames).toList()
            /*
            * Get set of objects with [fqNames] subtypes.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toSet(): Set<T> = toList().toSet()


            /*
             * Filter objects with [fqNames] subtypes by several annotations.
             * If [klasses] was not passed the list\set with result will contain only objects with [fqNames] subtypes.
             */
            inline fun <reified Q: T> withAnnotations(vararg klasses: KClass<out Annotation>) =
                WithAnnotations<Q>(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        /*
        * The class represents DSL for searching objects with several annotations.
        * Each item in the list\set with result will be casted to [T] type.
        * */
        class WithAnnotations<T: Any>(private val annotationFqNames: Set<String>, private val subtypeFqNames: Set<String>) {
            /*
            * Get list of objects with [subtypeFqNames] subtypes and [annotationFqNames] annotations.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toList(): List<T> = ReflektImpl.objects().withAnnotations<T>(annotationFqNames, subtypeFqNames).toList()
            /*
            * Get set of objects with [subtypeFqNames] subtypes and [annotationFqNames] annotations.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toSet(): Set<T> = toList().toSet()


            /*
             * Filter objects with [annotationFqNames] annotations by one subtype. All objects will be cast to [T] type.
             */
            inline fun <reified T: Any> withSubType() = WithSubTypes<T>(setOf(T::class.qualifiedName!!))
            /*
             * Filter objects with [annotationFqNames] annotations by several subtypes. All objects will be cast to [Any] type.
             */
            fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    /*
    * Find all classes in the project's modules and external libraries (that was marked as libraries to introspect)
    * and filter them by different conditions.
    * */
    class Classes {
        /*
         * Filter classes by one subtype. All classes will be cast to [T] type.
         */
        inline fun <reified T: Any> withSubType() = WithSubTypes<T>(setOf(T::class.qualifiedName!!))
        /*
         * Filter classes by several subtypes. All classes will be cast to [Any] type.
         * If [klasses] was not passed the list\set with result will be empty.
         */
        fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())


        /*
         * Filter classes by several annotations and subtype [T]. All classes will be cast to [T] type.
         * If [klasses] was not passed the list\set with result will contain only classes with subtype [T].
         */
        inline fun <reified T: Any> withAnnotations(vararg klasses: KClass<out Annotation>) =
            WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet(), setOf(T::class.qualifiedName!!))

        /*
        * The class represents DSL for searching classes with several subtypes.
        * Each item in the list\set with result will be casted to [T] type.
        * */
        class WithSubTypes<T: Any>(val fqNames: Set<String>) {
            /*
            * Get list of classes with [fqNames] subtypes.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withSubTypes<T>(fqNames).toList()
            /*
            * Get set of classes with [fqNames] subtypes.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toSet(): Set<KClass<T>> = toList().toSet()


            /*
             * Filter classes with [fqNames] subtypes by several annotations.
             * If [klasses] was not passed the list\set with result will contain only classes with [fqNames] subtypes.
             */
            inline fun <reified Q: T> withAnnotations(vararg klasses: KClass<out Annotation>) =
                WithAnnotations<Q>(klasses.mapNotNull { it.qualifiedName }.toSet(), fqNames)
        }

        /*
        * The class represents DSL for searching classes with several annotations.
        * Each item in the list\set with result will be casted to [T] type.
        * */
        class WithAnnotations<T: Any>(private val annotationFqNames: Set<String>, val subtypeFqNames: Set<String>) {
            /*
            * Get list of classes with [subtypeFqNames] subtypes and [annotationFqNames] annotations.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toList(): List<KClass<T>> = ReflektImpl.classes().withAnnotations<T>(annotationFqNames, subtypeFqNames).toList()
            /*
            * Get set of classes with [subtypeFqNames] subtypes and [annotationFqNames] annotations.
            * Each item in the list\set with result will be casted to [T] type.
            * */
            fun toSet(): Set<KClass<T>> = toList().toSet()


            /*
             * Filter classes with [annotationFqNames] annotations by one subtype. All classes will be cast to [T] type.
             */
            inline fun <reified T: Any> withSubType() = WithSubTypes<T>(subtypeFqNames)
            /*
             * Filter classes with [annotationFqNames] annotations by several subtypes. All classes will be cast to [Any] type.
             */
            fun withSubTypes(vararg klasses: KClass<out Any>) = WithSubTypes<Any>(klasses.mapNotNull { it.qualifiedName }.toSet())
        }
    }

    /*
    * Find all functions in the project's modules and external libraries (that was marked as libraries to introspect)
    * and filter them by different conditions.
    * */
    class Functions {
        /*
        * The class represents DSL for searching functions by the signature with several annotations.
        * */
        class WithAnnotations<T: Function<*>>(private val annotationFqNames: Set<String>) {
            /*
            * Get list of functions with [T] signature and [annotationFqNames] annotations.
            * */
            fun toList(): List<T> = ReflektImpl.functions().withAnnotations<T>(annotationFqNames).toList()
            /*
            * Get set of functions with [T] signature and [annotationFqNames] annotations.
            * */
            fun toSet(): Set<T> = toList().toSet()
        }


        /*
         * Filter functions with [T] signature by several annotations.
         * If [klasses] was not passed the list\set with result will contain only functions with [T] signature.
         */
        inline fun <reified T: Function<*>> withAnnotations(vararg klasses: KClass<out Annotation>) = WithAnnotations<T>(klasses.mapNotNull { it.qualifiedName }.toSet())
    }

    /*
    * The main function for searching objects. The chain of calls have to end with toList() or toSet() function.
    *
    * For example:
    *  Reflekt.objects().withSubType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    *  Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toSet()
    * */
    fun objects() = Objects()
    /*
    * The main function for searching classes. The chain of calls have to end with toList() or toSet() function.
    *
    * For example:
    *  Reflekt.classes().withSubType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
    *  Reflekt.classes().withAnnotations<AInterface>(FirstAnnotation::class).toSet()
    * */
    fun classes() = Classes()
    /*
    * The main function for searching functions. The chain of calls have to end with toList() or toSet() function.
    *
    * For example:
    *  Reflekt.functions().withAnnotations<() -> Unit>().toList()
    *  Reflekt.functions().withAnnotations<(Int, String) -> List<Int>>().toSet()
    * */
    fun functions() = Functions()
}
