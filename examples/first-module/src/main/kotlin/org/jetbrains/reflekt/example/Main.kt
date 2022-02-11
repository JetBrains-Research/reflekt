package org.jetbrains.reflekt.example

import org.jetbrains.kotlin.backend.common.ir.isTopLevel
import org.jetbrains.kotlin.ir.util.nameForIrSerialization
import org.jetbrains.reflekt.SmartReflekt

class Test(var a: List<Any>, b: List<Any>)

fun main() {
//    val tmp = Test(emptyList(), emptyList())
//    tmp.a = Reflekt.objects().withSuperType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class).toList()
//    println(tmp.a)
//
//    val objects = Reflekt.objects().withSuperType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
//    println(objects)
//    val objects1 = Reflekt.objects().withSuperType<AInterface>()
//        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
//    println(objects1)
//
//    val objects2 = Reflekt.objects().withSuperTypes(AInterface::class, A1::class)
//        .withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()
//    println(objects2)
//
//    val objects3 = Reflekt.objects().withSuperTypes(AInterface::class, A1::class)
//        .withAnnotations<AInterface>(FirstAnnotation::class).toList()
//    println(objects3)
//
//    val objects4 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toList()
//    println(objects4)
//    val objects5 = Reflekt.objects().withAnnotations<AInterface>(FirstAnnotation::class).toList()
//    println(objects5)
//    val objects6 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSupertype<AInterface>().toList()
//    println(objects6)
//    val objects7 = Reflekt.objects().withAnnotations<A1>(FirstAnnotation::class).withSupertypes(AInterface::class).toList()
//    println(objects7)
//    val objects8 = Reflekt.objects().withSuperType<AInterface>().toList()
//    println(objects8)
//
//    val classes1 = Reflekt.classes().withSuperType<AInterface>().toList()
//    println(classes1)
//    val classes2 = Reflekt.classes().withSuperType<BInterface>().toSet()
//    println(classes2)
//    val classes3 = Reflekt.classes().withAnnotations<B2>(FirstAnnotation::class, SecondAnnotation::class).toList()
//    println(classes3)
//
//    val classes4 = Reflekt.classes().withSuperType<Action>().toList()
//    println(classes4)
//
//    val functions = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotation::class).toList()
//    println(functions)

    val smartClasses = SmartReflekt.classes<BInterface>().filter { it.isData }.resolve()
    println(smartClasses)

    val smartObjects = SmartReflekt.objects<BInterface>().filter { it.isCompanion }.resolve()
    println(smartObjects)

    val smartFunctions = SmartReflekt.functions<() -> Unit>().filter { it.isTopLevel && it.nameForIrSerialization.asString() == "foo" }.resolve()
    println(smartFunctions)
    smartFunctions.forEach { it() }

    val fooBoolean = SmartReflekt.functions<() -> Boolean>().filter { it.isTopLevel && it.nameForIrSerialization.asString() == "fooBoolean" }.resolve().onEach { it() }
        .map { it.toString() }.toSet()
    println("fooBoolean: $fooBoolean")

    val fooStar = SmartReflekt.functions<(List<*>) -> Unit>().filter { it.isTopLevel && it.nameForIrSerialization.asString() == "withStar" }.resolve().onEach { it(listOf(1)) }
        .map { it.toString() }.toSet()
    println("fooStar: $fooStar")

    // TODO: we will support gnerics with bounds
//    val fooBound = SmartReflekt.functions<(Number) -> Unit>().filter { it.isTopLevel && it.name == "withBound" }.resolve().onEach { it(listOf(1)) }
//        .map { it.toString() }.toSet()
//    println("fooBound: $fooBound")

    /**
     * Such calls still fail, but it seems it's not a Reflekt problem since Kotlin doesn't consider our functions as subtypes of the given signature.
     */

//        val fooArray = SmartReflekt.functions<Function0<Array<*>>>().filter { it.isTopLevel && it.name == "fooArray" }.resolve().onEach { it() }.map { it.toString() }.toSet()
//        println(fooArray)
//
//        val fooList = SmartReflekt.functions<Function0<List<*>>>().filter { it.isTopLevel && it.name == "fooList" }.resolve().onEach { it() }.map { it.toString() }.toSet()
//        println(fooList)
//
//        val fooMyInClass = SmartReflekt.functions<Function0<MyInClass<*>>>().filter { it.isTopLevel && it.name == "fooMyInClass" }.resolve().onEach { it() }.map { it.toString() }.toSet()
//        println(fooMyInClass)

    /**
     * The simplest way to check it's to pass our functions as a parameter of an argument with the given signature:
     */
    //    arrayTestFun(::fooArray)
    //    listTestFun(::fooList)
    //    myInClassTestFun(::fooMyInClass)

    /**
     * For each of the functions Kotlin says [NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER] Not enough information to infer type variable T
     * Maybe we need to check https://kotlinlang.org/spec/type-system.html#mixed-site-variance
     */
}

fun arrayTestFun(funToTest: Function0<Array<*>>) {}

fun listTestFun(funToTest: Function0<List<*>>) {}

fun myInClassTestFun(funToTest: Function0<MyInClass<*>>) {}
