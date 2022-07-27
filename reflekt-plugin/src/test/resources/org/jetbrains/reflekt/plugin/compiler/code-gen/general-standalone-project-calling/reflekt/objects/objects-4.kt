// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withSuperTypes(A1::class).toList() },
    listOf(expectedReflektClass[A1::class]!!),
)
