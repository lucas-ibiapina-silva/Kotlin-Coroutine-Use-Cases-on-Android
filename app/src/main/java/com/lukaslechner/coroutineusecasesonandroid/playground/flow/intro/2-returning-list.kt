package com.lukaslechner.coroutineusecasesonandroid.playground.flow.intro

import kotlinx.coroutines.runBlocking
import java.math.BigInteger

fun main() = runBlocking {
    calculateFactorialOf(5).forEach {
        println("${System.currentTimeMillis()}: Intermediary result: $it")
    }
}

// factorial of n (n!) = 1 * 2 * 3 * 4 * ... * n
private fun calculateFactorialOf(number: Int) = buildList {
    var factorial = BigInteger.ONE
    for (i in 1..number) {
        Thread.sleep(10)
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        add(factorial)
    }
}