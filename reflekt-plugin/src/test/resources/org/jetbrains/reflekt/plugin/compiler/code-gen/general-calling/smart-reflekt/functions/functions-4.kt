// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.kotlin.backend.common.ir.isTopLevel
import org.jetbrains.reflekt.example.*


// TODO: support generics with bounds later. Currently this result will be empty
fun box(): String = checkCallResult(
    { SmartReflekt.functions<(Number) -> Unit>().filter { it.isTopLevel && it.name.asString() == "withBound" }.resolve() },
    listOf("A", "B")
)
