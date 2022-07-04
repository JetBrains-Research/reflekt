// FILE: TestCase.kt
import org.jetbrains.reflekt.SmartReflekt
import org.jetbrains.reflekt.test.helpers.checkCallResult
import org.jetbrains.kotlin.backend.common.ir.isTopLevel
import org.jetbrains.reflekt.example.*


// Such calls still fail, but it seems it's not a Reflekt problem since Kotlin doesn't consider our functions as subtypes of the given signature.
fun box(): String = checkCallResult(
    { SmartReflekt.functions<Function0<Array<*>>>().filter { it.isTopLevel && it.name.asString() == "fooArray" }.resolve() },
    listOf("A", "B")
)
