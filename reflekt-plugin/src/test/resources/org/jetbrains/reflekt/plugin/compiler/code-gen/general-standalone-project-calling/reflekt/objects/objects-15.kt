// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkObjectsCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withSuperType<A3>().withAnnotations<A3>(FirstAnnotation::class, SecondAnnotation::class).toList() },
    listOf("A3"),
    "org.jetbrains.reflekt.test.common",
)