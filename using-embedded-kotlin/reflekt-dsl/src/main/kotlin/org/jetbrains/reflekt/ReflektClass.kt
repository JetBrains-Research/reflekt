package org.jetbrains.reflekt

import kotlin.reflect.*

/**
 * Represents a class and designed for storing information about it, which is collected by Reflekt at compile-time.
 *
 * @param T the type of the class.
 */
interface ReflektClass<T : Any> {
    /**
     * [KClass] instance for this class.
     */
    val kClass: KClass<T>

    /**
     * Annotations which are present on this class. An important difference from [KClass.annotations] is that instances in this set are not the same as the
     * ones on the actual classes, so, can't be validly compared by using [Any.equals].
     */
    val annotations: Set<Annotation>

    /**
     * `true` if this class is `abstract`.
     */
    val isAbstract: Boolean

    /**
     * `true` if this class is a companion object.
     */
    val isCompanion: Boolean

    /**
     * `true` if this class is a data class.
     */
    val isData: Boolean

    /**
     * `true` if this class is `final`.
     */
    val isFinal: Boolean

    /**
     * `true` if this class is a Kotlin functional interface.
     */
    val isFun: Boolean

    /**
     * `true` if this class is an inner class.
     */
    val isInner: Boolean

    /**
     * `true` if this class is `open`.
     */
    val isOpen: Boolean

    /**
     * `true` if this class is `sealed`.
     */
    val isSealed: Boolean

    /**
     * `true` if this class is a value class.
     */
    val isValue: Boolean

    /**
     * The fully qualified dot-separated name of the class, or `null` if the class is local or a class of an anonymous object.
     */
    val qualifiedName: String?

    /**
     * The set of immediate superclasses of this class.
     */
    val superclasses: Set<ReflektClass<in T>>

    /**
     * The set of the immediate subclasses if this class is a sealed class, or an empty set otherwise.
     */
    val sealedSubclasses: Set<ReflektClass<out T>>

    /**
     * The simple name of the class as it was declared in the source code, or `null` if the class has no name (if, for example, it is a class of an anonymous
     * object).
     */
    val simpleName: String?

    /**
     * Visibility of this class, or `null` if its visibility cannot be represented in Kotlin.
     */
    val visibility: ReflektVisibility?

    /**
     * The instance of the object declaration, or `null` if this class is not an object declaration.
     */
    val objectInstance: T?
}

/**
 * Visibility is an aspect of a Kotlin declaration regulating where that declaration is accessible in the source code.
 * Visibility can be changed with one of the following modifiers: `public`, `protected`, `internal`, `private`.
 */
enum class ReflektVisibility {
    /**
     * Visibility of declarations marked with the `public` modifier, or with no modifier at all.
     */
    PUBLIC,

    /**
     * Visibility of declarations marked with the `protected` modifier.
     */
    PROTECTED,

    /**
     * Visibility of declarations marked with the `internal` modifier.
     */
    INTERNAL,

    /**
     * Visibility of declarations marked with the `private` modifier.
     */
    PRIVATE,
    ;
}

@InternalReflektApi
data class ReflektClassImpl<T : Any>(
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

/**
 * Represents a [ReflektClass], which definitely describes an `object` declaration.
 *
 * @param reflektClass The delegated [ReflektClass], [ReflektClass.objectInstance] of it must not be `null`.
 */
@JvmInline
value class ReflektObject<T : Any> @InternalReflektApi constructor(private val reflektClass: ReflektClass<T>) : ReflektClass<T> by reflektClass {
    override val objectInstance: T
        get() = checkNotNull(reflektClass.objectInstance) { "Object instance of $reflektClass isn't available" }

    init {
        requireNotNull(reflektClass.objectInstance) { "There must be an object instance in $reflektClass" }
    }

    override fun toString(): String = reflektClass.toString()
}
