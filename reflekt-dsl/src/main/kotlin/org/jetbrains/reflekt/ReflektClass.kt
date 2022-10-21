package org.jetbrains.reflekt

import kotlin.reflect.*

/**
 * Represents a class and designed for storing information about it, which is collected by Reflekt at compile-time.
 *
 * @param T the type of the class.
 */
public interface ReflektClass<T : Any> : ReflektAnnotatedElement {
    /**
     * [KClass] instance for this class.
     */
    public val kClass: KClass<T>

    /**
     * `true` if this class is `abstract`.
     */
    public val isAbstract: Boolean

    /**
     * `true` if this class is a companion object.
     */
    public val isCompanion: Boolean

    /**
     * `true` if this class is a data class.
     */
    public val isData: Boolean

    /**
     * `true` if this class is `final`.
     */
    public val isFinal: Boolean

    /**
     * `true` if this class is a Kotlin functional interface.
     */
    public val isFun: Boolean

    /**
     * `true` if this class is an inner class.
     */
    public val isInner: Boolean

    /**
     * `true` if this class is `open`.
     */
    public val isOpen: Boolean

    /**
     * `true` if this class is `sealed`.
     */
    public val isSealed: Boolean

    /**
     * `true` if this class is a value class.
     */
    public val isValue: Boolean

    /**
     * The fully qualified dot-separated name of the class, or `null` if the class is local or a class of an anonymous object.
     */
    public val qualifiedName: String?

    /**
     * The set of immediate superclasses of this class.
     */
    public val superclasses: Set<ReflektClass<in T>>

    /**
     * The set of the immediate subclasses if this class is a sealed class, or an empty set otherwise.
     */
    public val sealedSubclasses: Set<ReflektClass<out T>>

    /**
     * The simple name of the class as it was declared in the source code, or `null` if the class has no name (if, for example, it is a class of an anonymous
     * object).
     */
    public val simpleName: String?

    /**
     * Visibility of this class, or `null` if its visibility cannot be represented in Kotlin.
     */
    public val visibility: ReflektVisibility?

    /**
     * The instance of the object declaration, or `null` if this class is not an object declaration.
     */
    public val objectInstance: T?
}

/**
 * Basic implementation of [ReflektClass] as a tuple of constants, normally should be instantiated only by Reflekt.
 */
@InternalReflektApi
public data class ReflektClassImpl<T : Any>(
    override val kClass: KClass<T>,
    override val annotations: MutableSet<Annotation> = HashSet(),
    override val isAbstract: Boolean = false,
    override val isCompanion: Boolean = false,
    override val isData: Boolean = false,
    override val isFinal: Boolean = true,
    override val isFun: Boolean = false,
    override val isInner: Boolean = false,
    override val isOpen: Boolean = false,
    override val isSealed: Boolean = false,
    override val isValue: Boolean = false,
    override val qualifiedName: String?,
    override val superclasses: MutableSet<ReflektClass<in T>> = HashSet(),
    override val sealedSubclasses: MutableSet<ReflektClass<out T>> = HashSet(),
    override val simpleName: String?,
    override val visibility: ReflektVisibility? = ReflektVisibility.PUBLIC,
    override val objectInstance: T? = null,
) : ReflektClass<T> {
    override fun toString(): String = "ReflektClassImpl(" +
        "kClass=$kClass, " +
        "annotations=$annotations, " +
        "isAbstract=$isAbstract, " +
        "isCompanion=$isCompanion, " +
        "isData=$isData, " +
        "isFinal=$isFinal, " +
        "isFun=$isFun, " +
        "isInner=$isInner, " +
        "isOpen=$isOpen, " +
        "isSealed=$isSealed, " +
        "isValue=$isValue, " +
        "qualifiedName=$qualifiedName, " +
        "superclasses=$superclasses, " +
        "sealedSubclasses=${sealedSubclasses.map { it.qualifiedName }}, " +
        "simpleName=$simpleName, " +
        "visibility=$visibility, " +
        "objectInstance=$objectInstance" +
        ")"
}
