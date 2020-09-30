package io.reflekt

import kotlin.reflect.KClass

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
        fun <T: Any> withSubType(fqName: String) = Classes.WithSubType<T>(fqName)

        class WithSubType<T: Any>(val fqName: String) {
            class WithAnnotation<T: Annotation>(private val fqName: String, val withSubtypeFqName: String) {
                fun toList(): List<T> = error("This method should be replaced during compilation")
                fun toSet(): Set<T> = toList().toSet()
            }

            fun <T: Annotation> withAnnotation(fqName: String, withSubtypeFqName: String) = WithAnnotation<T>(fqName, withSubtypeFqName)
            fun toList(): List<KClass<T>> =  error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    fun objects() = Objects()
    fun classes() = Classes()
}
