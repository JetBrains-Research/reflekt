package io.reflekt.plugin.analysis


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

fun function3(s: Pair<*, *>) {
    println("Hello")
}

// проблема с Pair, ArrayList
