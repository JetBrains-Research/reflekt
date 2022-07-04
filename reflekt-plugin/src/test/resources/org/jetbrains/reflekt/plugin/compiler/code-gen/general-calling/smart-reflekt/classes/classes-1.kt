// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.reflekt.example.*

fun box(): String = checkCallResult(
    { SmartReflekt.classes<BInterface>().filter { it.isData }.resolve() },
    listOf("A", "B")
)
