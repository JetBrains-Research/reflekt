// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withSuperTypes(AInterface::class).toList() },
    listOf(expectedReflektClass[A1::class]!!, expectedReflektClass[A2::class]!!, expectedReflektClass[A3::class]!!),
)
