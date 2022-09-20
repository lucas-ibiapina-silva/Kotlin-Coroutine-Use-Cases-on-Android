package com.lukaslechner.coroutineusecasesonandroid.playground.structuredconcurrency

import kotlinx.coroutines.*

fun main() = runBlocking {
    val scope = CoroutineScope(Dispatchers.Default)

    scope.coroutineContext[Job]!!.invokeOnCompletion {
        if(it is CancellationException) {
            println("parent job was cancelled")
        }
    }

    val childCoroutine1Job = scope.launch {
        delay(1000)
        println("coroutine 1 compledted")
    }

    childCoroutine1Job.invokeOnCompletion { throwable ->
        if(throwable is CancellationException) {
            println("coroutine 1 was cancelled")
        }
    }

    scope.launch {
        delay(1000)
        println("coroutine 2 compledted")
    }.invokeOnCompletion { throwable ->
        if(throwable is CancellationException) {
            println("coroutine 2 was cancelled")
        }
    }

    delay(200)
    childCoroutine1Job.cancelAndJoin()

    //scope.coroutineContext[Job]!!.cancelAndJoin()
}