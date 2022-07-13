package com.lukaslechner.coroutineusecasesonandroid.playground.flow.intro

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigInteger

fun main() = runBlocking {

    launch {
        calculateFactorialOf(5).collect {
            println("${System.currentTimeMillis()}: Intermediary result: $it")
        }
    }

    println("Ready for more work")
}

// factorial of n (n!) = 1 * 2 * 3 * 4 * ... * n
private suspend fun calculateFactorialOf(number: Int) = flow {
    var factorial = BigInteger.ONE
    for (i in 1..number) {
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        delay(10)
        emit(factorial)
    }
}.flowOn(Dispatchers.Default)