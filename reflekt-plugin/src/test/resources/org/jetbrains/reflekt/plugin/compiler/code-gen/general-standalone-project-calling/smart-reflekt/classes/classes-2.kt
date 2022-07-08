// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkClassesCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = checkClassesCallResult(
    { SmartReflekt.classes<BInterface>().filter { it.isData }.resolve() },
    listOf("B2"),
    "org.jetbrains.reflekt.test.common",
)
