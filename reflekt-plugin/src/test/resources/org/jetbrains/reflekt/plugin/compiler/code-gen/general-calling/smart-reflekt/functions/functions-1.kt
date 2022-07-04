// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.kotlin.backend.common.ir.isTopLevel
import org.jetbrains.reflekt.example.*


fun box(): String = checkCallResult(
    { SmartReflekt.functions<() -> Unit>().filter { it.isTopLevel && it.name.asString() == "foo" }.resolve() },
    listOf("A", "B")
)
