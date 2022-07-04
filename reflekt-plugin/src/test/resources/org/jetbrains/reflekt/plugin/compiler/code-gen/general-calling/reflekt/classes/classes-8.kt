// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { Reflekt.classes().withSuperType<Any>().toList() },
    listOf("B1", "B2", "B3", "B3.B4", "TestFunctions", "MyInClass"),
    "org.jetbrains.reflekt.test.common",
)
