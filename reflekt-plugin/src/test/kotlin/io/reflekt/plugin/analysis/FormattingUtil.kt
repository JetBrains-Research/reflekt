package io.reflekt.plugin.analysis

fun CharSequence.indentN(n: Int): String {
    val indent = "\t".repeat(n)
    return "$indent${this.toString().replace("\n", "\n$indent")}"
}

fun <T : Any?> Collection<T>.joinToStringIndented(
    brackets: Brackets = Brackets.SQUARE,
    transform: (T) -> CharSequence = { it.toString() }
): String {
    return if (this.isEmpty()) {
        brackets.left + brackets.right
    } else {
        this.map { transform(it) }
            .sortedBy { it.toString() }
            .joinToString(separator = ",\n", prefix = "${brackets.left}\n", postfix = "\n${brackets.right}") { it.indentN(1) }
    }
}

fun <K : Any?, V : Any?> Map<K, V>.joinToStringIndented(transform: (K, V) -> CharSequence): String {
    return toList().joinToStringIndented(brackets = Brackets.CURLY) { (k, v) -> transform(k, v) }
}

enum class Brackets(val left: String, val right: String) {
    SQUARE("[", "]"),
    CURLY("{", "}"),
    ROUND("(", ")")
}
