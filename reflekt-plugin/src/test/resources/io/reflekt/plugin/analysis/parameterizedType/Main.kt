package io.reflekt.plugin.analysis.parameterizedType


// ParameterizedType(
//   fqName=kotlin.Function1,
//   superTypeFqNames=[kotlin.Function1, kotlin.Function, kotlin.Any],
//   parameters=[
//     ParameterizedType(
//       fqName=kotlin.collections.List,
//       superTypeFqNames=[kotlin.collections.Collection, kotlin.collections.Iterable, kotlin.Any, kotlin.collections.List],
//       parameters=[
//         ParameterizedType(
//           fqName=kotlin.Any,
//           superTypeFqNames=[kotlin.Any],
//           parameters=[],
//           variance=OUT,
//           nullable=true
//         )
//       ],
//       variance=IN,
//       nullable=false
//     ),
//     ParameterizedType(
//       fqName=kotlin.Unit,
//       superTypeFqNames=[kotlin.Any, kotlin.Unit],
//       parameters=[],
//       variance=OUT,
//       nullable=false
//     )
//   ],
//   variance=INVARIANT,
//   nullable=false
// )
//fun function(s: List<*>) {
//    println("Hello")
//}

//fun function2(s: List<*>) {
//    println("Hello")
//}


open class A<in T, S>() {

    //
    //  ParameterizedType(
    //  fqName=kotlin.Function2,
    //  superTypes=[],
    //  parameters=[
    //    ParameterizedType(
    //      fqName=io.reflekt.plugin.analysis.parameterizedType.A,
    //      superTypes=[
    //        ParameterizedType(
    //          fqName=kotlin.Any,
    //          superTypes=[],
    //          parameters=[],
    //          variance=INVARIANT,
    //          nullable=false
    //          )
    //       ],
    //       parameters=[
    //         ParameterizedType(
    //           fqName=kotlin.Any,
    //           superTypes=[
    //             ParameterizedType(
    //               fqName=kotlin.Any,
    //               superTypes=[],
    //               parameters=[],
    //               variance=INVARIANT,
    //               nullable=true
    //             )
    //           ],
    //           parameters=[],
    //           variance=IN,
    //           nullable=true
    //        ),
    //        ParameterizedType(
    //          fqName=kotlin.Any,
    //          superTypes=[
    //            ParameterizedType(
    //              fqName=kotlin.Any,
    //              superTypes=[],
    //              parameters=[],
    //              variance=INVARIANT,
    //              nullable=true
    //            )
    //          ],
    //          parameters=[],
    //          variance=INVARIANT,
    //          nullable=true
    //        )
    //     ],
    //     variance=IN,
    //     nullable=false
    //   ),
    //   ParameterizedType(fqName=kotlin.Any, superTypes=[ParameterizedType(fqName=kotlin.Any, superTypes=[], parameters=[], variance=INVARIANT, nullable=true)], parameters=[], variance=IN, nullable=true), ParameterizedType(fqName=kotlin.Unit, superTypes=[ParameterizedType(fqName=kotlin.Any, superTypes=[], parameters=[], variance=INVARIANT, nullable=false)], parameters=[], variance=OUT, nullable=false)], variance=INVARIANT, nullable=false)

//    fun set(a: T) {
//        print(a)
//    }
}

class A1<T> : A<T, T>() {

}

class B<out T>(private val b: T) {
//    fun get(): T {
//        return b
//    }
}

fun functionA(a: A1<Number>) {
    println("Hello")
}


open class Foo<T: Bar>

class Bar : Foo<Bar>()

fun functionB(b: Array<out Number>) {
}

// List<*> говорится, что это List<Any?>, на самом деле должно быть List<out Any?> что верно в целом
// A<*> говорится, что это A<in Any>, на самом деле должно быть A<in Nothing>
// Array<*> -> его никак нельзя описать одним параметризированным типом, так как оно себя ведет по-разному в зависимости от действия

// Array<*>
// ParametrizedType(fqName=kotlin.Array, superTypeFqNames=[kotlin.Any, kotlin.Cloneable, java.io.Serializable, kotlin.Array], parameters = ParametrizedType.STAR, variance = Variance.STAR, nullable = true)
//

// in + in(redundant)/invariance T | * -> in T | in Nothing
// out + out(redundant)/invariance T | * -> out T | out Any?
// inv + in | out | * -> in T | out T | *


// 1. имя должно быть одинаковое или супертипом того
fun main(s: List<Int>) {
    val a: Array<out Int> = arrayOf()
    functionB(a)

//    Array<object> не супертип array<string>
}

// проблема с Pair, ArrayList
