// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.kotlin.backend.common.ir.isTopLevel
import org.jetbrains.reflekt.example.*


fun box(): String = checkCallResult(
    { SmartReflekt.functions<(List<*>) -> Unit>().filter { it.isTopLevel && it.name.asString() == "withStar" }.resolve() },
    listOf("A", "B")
)
