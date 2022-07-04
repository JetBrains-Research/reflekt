// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult

fun box(): String = checkCallResult(
    { SmartReflekt.objects<Any>().filter { it.isCompanion }.resolve() },
    listOf("A", "B")
)
