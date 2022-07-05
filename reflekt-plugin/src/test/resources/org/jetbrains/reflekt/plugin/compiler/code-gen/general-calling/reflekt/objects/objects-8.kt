// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkObjectsCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkObjectsCallResult(
    { Reflekt.objects().withSuperType<Any>().toList() },
    listOf("A1", "A2", "A3", "A4", "TestFunctions.Companion"),
    "org.jetbrains.reflekt.test.common",
)
