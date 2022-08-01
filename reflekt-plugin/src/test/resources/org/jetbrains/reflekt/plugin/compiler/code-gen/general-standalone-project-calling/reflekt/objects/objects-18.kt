// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.*
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withSuperTypes(A3::class).withAnnotations<A3>().toList() },
    listOf(expectedReflektClass[A3::class]!!),
)
