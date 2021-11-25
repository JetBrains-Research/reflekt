package org.jetbrains.reflekt.plugin.analysis

/**
 * @property left
 * @property right
 */
enum class Brackets(val left: String, val right: String) {
    CURLY("{", "}"),
    ROUND("(", ")"),
    SQUARE("[", "]"),
    ;
}

fun CharSequence.indentN(n: Int): String {
    val indent = "\t".repeat(n)
    return "$indent${this.toString().replace("\n", "\n$indent")}"
}

fun <T : Any?> Collection<T>.joinToStringIndented(
    brackets: Brackets = Brackets.SQUARE,
    transform: (T) -> CharSequence = { it.toString() },
): String = if (this.isEmpty()) {
    brackets.left + brackets.right
} else {
    this.map { transform(it) }
        .sortedBy { it.toString() }
        .joinToString(separator = ",\n", prefix = "${brackets.left}\n", postfix = "\n${brackets.right}") { it.indentN(1) }
}

fun <K : Any?, V : Any?> Map<K, V>.joinToStringIndented(transform: (K, V) -> CharSequence): String =
        toList().joinToStringIndented(brackets = Brackets.CURLY) { (k, v) -> transform(k, v) }
