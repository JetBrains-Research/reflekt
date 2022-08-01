@file:OptIn(InternalReflektApi::class)

package org.jetbrains.reflekt.test.helpers

import org.jetbrains.reflekt.*
import org.jetbrains.reflekt.test.common.*
import kotlin.reflect.KClass

val expectedReflektClass: Map<KClass<*>, ReflektClass<*>> = hashMapOf(
    Any::class to ReflektClassImpl(
        kClass = Any::class,
        isFinal = false,
        isOpen = true,
        qualifiedName = "kotlin.Any",
        simpleName = "Any",
    ),
    AInterface::class to ReflektClassImpl(
        kClass = AInterface::class,
        isAbstract = true,
        isFinal = false,
        qualifiedName = "org.jetbrains.reflekt.test.common.AInterface",
        simpleName = "AInterface",
    ),
    BInterface::class to ReflektClassImpl(
        kClass = BInterface::class,
        isAbstract = true,
        isFinal = false,
        qualifiedName = "org.jetbrains.reflekt.test.common.BInterface",
        simpleName = "BInterface",
    ),
    B1::class to ReflektClassImpl(kClass = B1::class, qualifiedName = "org.jetbrains.reflekt.test.common.B1", simpleName = "B1"),
    B2::class to ReflektClassImpl(
        kClass = B2::class,
        annotations = hashSetOf(
            SecondAnnotation(
                message = "Test",
                first = FirstAnnotation(int = 42, array = booleanArrayOf(false, true))
            ),
            FirstAnnotation(int = 42, array = booleanArrayOf()),
        ),
        isData = true,
        qualifiedName = "org.jetbrains.reflekt.test.common.B2",
        simpleName = "B2",
    ),
    B3::class to ReflektClassImpl(
        kClass = B3::class,
        annotations = hashSetOf(SecondAnnotation(message = "Test"), FirstAnnotation()),
        qualifiedName = "org.jetbrains.reflekt.test.common.B3",
        simpleName = "B3",
    ),
    B3.B4::class to ReflektClassImpl(kClass = B3.B4::class, qualifiedName = "org.jetbrains.reflekt.test.common.B3.B4", simpleName = "B4"),
    TestFunctions::class to ReflektClassImpl(
        kClass = TestFunctions::class,
        qualifiedName = "org.jetbrains.reflekt.test.common.TestFunctions",
        simpleName = "TestFunctions",
    ),
    MyInClass::class to ReflektClassImpl(
        kClass = MyInClass::class,
        qualifiedName = "org.jetbrains.reflekt.test.common.MyInClass",
        simpleName = "MyInClass",
    ),
    A1::class to ReflektClassImpl(kClass = A1::class, qualifiedName = "org.jetbrains.reflekt.test.common.A1", simpleName = "A1", objectInstance = A1),
    A2::class to ReflektClassImpl(
        kClass = A2::class,
        annotations = hashSetOf(SecondAnnotation(message = "Test")),
        qualifiedName = "org.jetbrains.reflekt.test.common.A2",
        simpleName = "A2",
        objectInstance = A2,
    ),
    A3::class to ReflektClassImpl(
        kClass = A3::class,
        annotations = hashSetOf(FirstAnnotation()),
        qualifiedName = "org.jetbrains.reflekt.test.common.A3",
        simpleName = "A3",
        objectInstance = A3,
    ),
    A4::class to ReflektClassImpl(
        kClass = A4::class,
        annotations = hashSetOf(SecondAnnotation(message = "Test")),
        qualifiedName = "org.jetbrains.reflekt.test.common.A4",
        simpleName = "A4",
        objectInstance = A4,
    ),
    TestFunctions.Companion::class to ReflektClassImpl(
        kClass = TestFunctions.Companion::class,
        isCompanion = true,
        qualifiedName = "org.jetbrains.reflekt.test.common.TestFunctions.Companion",
        simpleName = "Companion",
        objectInstance = TestFunctions.Companion,
    ),
).also {
    (it[AInterface::class]!! as ReflektClassImpl<AInterface>).superclasses += it[Any::class] as ReflektClass<in AInterface>
    (it[BInterface::class]!! as ReflektClassImpl<BInterface>).superclasses += it[Any::class] as ReflektClass<in BInterface>
    (it[B1::class]!! as ReflektClassImpl<B1>).superclasses += it[BInterface::class] as ReflektClass<in B1>
    (it[B2::class]!! as ReflektClassImpl<B2>).superclasses += it[BInterface::class] as ReflektClass<in B2>
    (it[B3::class]!! as ReflektClassImpl<B3>).superclasses += it[BInterface::class] as ReflektClass<in B3>
    (it[B3.B4::class]!! as ReflektClassImpl<B3.B4>).superclasses += it[BInterface::class] as ReflektClass<in B3.B4>
    (it[TestFunctions::class]!! as ReflektClassImpl<TestFunctions>).superclasses += it[Any::class] as ReflektClass<in TestFunctions>
    (it[MyInClass::class]!! as ReflektClassImpl<MyInClass<*>>).superclasses += it[Any::class] as ReflektClass<in MyInClass<*>>
    (it[A1::class]!! as ReflektClassImpl<A1>).superclasses += it[AInterface::class] as ReflektClass<in A1>
    (it[A2::class]!! as ReflektClassImpl<A2>).superclasses += it[AInterface::class] as ReflektClass<in A2>
    (it[A3::class]!! as ReflektClassImpl<A3>).superclasses += it[AInterface::class] as ReflektClass<in A3>
    (it[A4::class]!! as ReflektClassImpl<A4>).superclasses += it[BInterface::class] as ReflektClass<in A4>
    (it[TestFunctions.Companion::class]!! as ReflektClassImpl<TestFunctions.Companion>).superclasses += it[Any::class] as ReflektClass<in TestFunctions.Companion>
}
