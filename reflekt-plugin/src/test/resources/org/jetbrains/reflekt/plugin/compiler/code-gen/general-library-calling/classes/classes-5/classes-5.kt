// FILE: TestCase.kt
import org.jetbrains.reflekt.Reflekt
import org.jetbrains.reflekt.test.common.*

fun main() {
    Reflekt.classes().withSuperType<AInterface>().toList()
}
