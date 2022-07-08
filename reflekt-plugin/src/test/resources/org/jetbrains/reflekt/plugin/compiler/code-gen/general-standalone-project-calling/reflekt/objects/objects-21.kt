// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkObjectsCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withAnnotations<A1>().withSupertype<A1>().toList() },
    listOf("A1"),
    "org.jetbrains.reflekt.test.common",
)
