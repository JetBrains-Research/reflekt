// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withAnnotations<B1>().withSupertype<B1>().toList() },
    listOf(expectedReflektClass[B1::class]!!),
)
