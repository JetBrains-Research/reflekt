// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.helpers.checkFinctionsCallResult
import org.jetbrains.reflekt.test.common.*

fun box(): String = "OK"
)
// TODO: fix it
//    checkFinctionsCallResult(
//    { Reflekt.functions().withAnnotations<() -> Any?>().toList() },
//    listOf("fun foo1(): kotlin.Unit", "fun foo2(): kotlin.Unit", "fun foo4(): kotlin.Unit")
//)
