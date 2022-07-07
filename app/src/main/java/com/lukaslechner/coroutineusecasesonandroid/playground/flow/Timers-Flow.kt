package com.lukaslechner.coroutineusecasesonandroid.playground.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val startTime = System.currentTimeMillis()

    val flow = flow {
        var curVal = 0
        while (true) {
            delay(300)
            emit(curVal++)
        }
    }.conflate().flowOn(Dispatchers.Default)

    flow.take(3).collect {
        delay(500)
        println("${System.currentTimeMillis()} - $it")
    }

    val endTime = System.currentTimeMillis()

    println("total time: ${endTime - startTime}")

}