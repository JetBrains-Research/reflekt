import org.jetbrains.reflekt.Reflekt

annotation class FirstAnnotation

annotation class SecondAnnotation(val message: String)

@FirstAnnotation
fun foo() {
    println("public first example foo")
}

@FirstAnnotation
private fun barPrivate() {
    println("private first example bar")
}

fun box(): String {
    val functions = Reflekt.functions().withAnnotations<() -> Unit>(FirstAnnotation::class).toList()
    println(functions)
    return if (functions.toString() == "Some results") { "OK" } else { "Fail: $functions" }
}
