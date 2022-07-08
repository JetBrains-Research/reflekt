// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.common.*

fun main() {
    Reflekt.classes().withSuperTypes(B2::class).toList()
}
