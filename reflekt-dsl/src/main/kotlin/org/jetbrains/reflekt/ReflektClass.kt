package org.jetbrains.reflekt

import kotlin.reflect.*

/**
 * Represents a class and designed for storing information about it, which is collected by Reflekt at compile-time.
 *
 * @param T the type of the class.
 * @property kClass [KClass] instance for this class.
 * @property annotations Annotations which are present on this class.
 * @property isAbstract `true` if this class is `abstract`.
 * @property isCompanion `true` if this class is a companion object.
 * @property isData `true` if this class is a data class.
 * @property isFinal `true` if this class is `final`.
 * @property isFun `true` if this class is a Kotlin functional interface.
 * @property isInner `true` if this class is an inner class.
 * @property isOpen `true` if this class is `open`.
 * @property isSealed `true` if this class is `sealed`.
 * @property isValue `true` if this class is a value class.
 * @property qualifiedName The fully qualified dot-separated name of the class, or `null` if the class is local or a class of an anonymous object.
 * @property superclasses The set of immediate superclasses of this class.
 * @property sealedSubclasses The set of the immediate subclasses if this class is a sealed class, or an empty set otherwise.
 * @property simpleName The simple name of the class as it was declared in the source code,
 * or `null` if the class has no name (if, for example, it is a class of an anonymous object).
 * @property visibility Visibility of this class, or `null` if its visibility cannot be represented in Kotlin.
 */
interface ReflektClass<T : Any> {
    val kClass: KClass<T>
    val annotations: Set<KClass<out Annotation>>
    val isAbstract: Boolean
    val isCompanion: Boolean
    val isData: Boolean
    val isFinal: Boolean
    val isFun: Boolean
    val isInner: Boolean
    val isOpen: Boolean
    val isSealed: Boolean
    val isValue: Boolean
    val qualifiedName: String?
    val superclasses: Set<ReflektClass<in T>>
    val sealedSubclasses: Set<ReflektClass<out T>>
    val simpleName: String?
    val visibility: KVisibility?
}

internal data class ReflektClassImpl<T : Any>(
    override val kClass: KClass<T>,
    override val annotations: MutableSet<KClass<out Annotation>>,
    override val isAbstract: Boolean,
    override val isCompanion: Boolean,
    override val isData: Boolean,
    override val isFinal: Boolean,
    override val isFun: Boolean,
    override val isInner: Boolean,
    override val isOpen: Boolean,
    override val isSealed: Boolean,
    override val isValue: Boolean,
    override val qualifiedName: String?,
    override val superclasses: MutableSet<ReflektClass<in T>>,
    override val sealedSubclasses: MutableSet<ReflektClass<out T>>,
    override val simpleName: String?,
    override val visibility: KVisibility?,
) : ReflektClass<T>
