package io.arrowkt.example

annotation class Route

//metadebug

@Route
fun a() = println("test A")

@Route
fun b() = println("test B")


object AllRoutes {

  fun init() {
    println("AllRoutes")
  }

}


fun main() {
   AllRoutes.init()
}
