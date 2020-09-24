package io.reflekt

import kotlin.reflect.KClass

object ReflektImpl {
    class Objects {
        fun <T> withSubType(fqName: String) = Objects.WithSubType<T>(fqName)

        class WithSubType<T>(val fqName: String) {
            class WithAnnotation<T>(private val fqName: String, val withSubtypeFqName: String) {
                fun toList(): List<T> = error("This method should be replaced during compilation")
                fun toSet(): Set<T> = toList().toSet()
            }

            fun <T> withAnnotation(fqName: String, withSubtypeFqName: String) = WithAnnotation<T>(fqName, withSubtypeFqName)
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T: Any> withSubType(fqName: String) = Classes.WithSubType<T>(fqName)

        class WithSubType<T: Any>(private val fqName: String) {
            class WithAnnotation<T: Annotation>(private val fqName: String) {
                fun toList(): List<T> = error("This method should be replaced during compilation")
                fun toSet(): Set<T> = toList().toSet()
            }

            fun <T: Annotation> withAnnotation(fqName: String) = WithAnnotation<T>(fqName)
            fun toList(): List<KClass<T>> =  error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    fun objects() = Objects()
    fun classes() = Classes()
}
