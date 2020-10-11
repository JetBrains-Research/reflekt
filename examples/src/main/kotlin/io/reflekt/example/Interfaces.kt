package io.reflekt.example

interface AInterface {
    fun description(): String
}

interface AInterface1: AInterface {
    override fun description(): String
}

interface BInterface {
}
