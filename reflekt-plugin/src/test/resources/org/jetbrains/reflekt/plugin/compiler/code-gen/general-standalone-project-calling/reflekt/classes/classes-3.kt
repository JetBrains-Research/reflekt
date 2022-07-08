// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperTypes(BInterface::class).toList() },
    listOf("B1", "B2", "B3", "B3.B4"),
    "org.jetbrains.reflekt.test.common",
)
