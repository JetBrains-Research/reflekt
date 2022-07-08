// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkObjectsCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withSuperTypes(A3::class).withAnnotations<A3>().toList() },
    listOf("A3"),
    "org.jetbrains.reflekt.test.common",
)