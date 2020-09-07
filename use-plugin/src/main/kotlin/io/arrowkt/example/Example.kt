package io.arrowkt.example

annotation class Route

//metadebug

@Route
fun a() = println("test A")

@Route
fun b() = println("test B")


object AllRoutes {
//
//  fun init() {
//    println("AllRoutes")
//  }

    // Todo: return List<Unit> instead of () -> List<Unit>
    fun init(): () -> List<Unit> {
        println("AllRoutes")
        TODO()
    }

}


fun main() {
   println(AllRoutes.init()())
}
