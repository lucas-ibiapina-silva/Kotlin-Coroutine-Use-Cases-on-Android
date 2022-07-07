package com.lukaslechner.coroutineusecasesonandroid.playground.flow

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val folding = listOf(1, 2, 3).runningFold(0) { acc, cur ->
        acc + cur
    }

    println("folding: $folding")

    val reduce = listOf(1, 2, 3).runningReduce { acc, i ->
        acc + i
    }

    println("reducing: $reduce")

    flow {
        emit(Stock("Alphabet", 1.00f))
        emit(Stock("Alphabet", 2.00f))
        emit(Stock("Alphabet", 1.00f))
        emit(Stock("Alphabet", 4.00f))
        emit(Stock("Alphabet", 2.00f))
    }.runningReduce{ last, current ->
        println("Entering RunningReduce")
        return@runningReduce if (current.priceUsd < last.priceUsd) {
            current.copy(goesUp = false)
        } else {
            current.copy(goesUp = true)
        }
    }.collect {
        println(it)
    }


}

data class Stock(val name: String, val priceUsd: Float, val goesUp: Boolean = false)