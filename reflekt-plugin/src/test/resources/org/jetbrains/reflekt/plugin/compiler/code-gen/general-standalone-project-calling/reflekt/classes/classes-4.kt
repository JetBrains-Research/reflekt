// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperTypes(B2::class).toList() },
    listOf("B2"),
    "org.jetbrains.reflekt.test.common",
)
