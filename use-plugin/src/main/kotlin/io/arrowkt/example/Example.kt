package io.arrowkt.example

annotation class Route

//metadebug

@Route
fun A() = println("test A")

@Route
fun B() = println("test B")


fun main() {
  // Call A and print an additional line
  B()
}
