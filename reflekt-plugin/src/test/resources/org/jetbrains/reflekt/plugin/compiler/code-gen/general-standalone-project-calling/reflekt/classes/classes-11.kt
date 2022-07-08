// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withAnnotations<BInterface>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf(expectedReflektClass[B2::class]!!, expectedReflektClass[B3::class]!!),
)
