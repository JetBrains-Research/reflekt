// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperType<Any>().toList() },
    listOf(
        expectedReflektClass[B1::class]!!,
        expectedReflektClass[B2::class]!!,
        expectedReflektClass[B3::class]!!,
        expectedReflektClass[B3.B4::class]!!,
        expectedReflektClass[TestFunctions::class]!!,
        expectedReflektClass[MyInClass::class]!!
    ),
)
