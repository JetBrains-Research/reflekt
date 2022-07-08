// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withAnnotations<BInterface>().toList() },
    listOf(expectedReflektClass[B1::class]!!, expectedReflektClass[B2::class]!!, expectedReflektClass[B3::class]!!, expectedReflektClass[B3.B4::class]!!),
)
