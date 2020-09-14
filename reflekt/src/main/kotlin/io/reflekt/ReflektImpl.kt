package io.reflekt

import kotlin.reflect.KClass

object ReflektImpl {
    class Objects {
        fun <T> withSubType() = Objects.WithSubType<T>()

        class WithSubType<T> {
            fun toList(): List<T> = error("This method should be replaced during compilation")
            fun toSet(): Set<T> = toList().toSet()
        }
    }

    class Classes {
        fun <T: Any> withSubType() = Classes.WithSubType<T>()

        class WithSubType<T: Any> {
            fun toList(): List<KClass<T>> =  error("This method should be replaced during compilation")
            fun toSet(): Set<KClass<T>> = toList().toSet()
        }
    }

    fun objects() = Objects()

    fun classes() = Classes()
}
