package com.lukaslechner.coroutineusecasesonandroid.playground.flow.intro

import java.math.BigInteger

fun main() {
    calculateFactorialOf(5).forEach {
        println("${System.currentTimeMillis()}: Intermediary result: $it")
    }
    println("Ready for more work")
}

// factorial of n (n!) = 1 * 2 * 3 * 4 * ... * n
private fun calculateFactorialOf(number: Int) = sequence {
    var factorial = BigInteger.ONE
    for (i in 1..number) {
        Thread.sleep(100)
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        yield(factorial)
    }
}