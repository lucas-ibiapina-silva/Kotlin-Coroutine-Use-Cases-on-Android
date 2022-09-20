package com.lukaslechner.coroutineusecasesonandroid.playground.structuredconcurrency

import kotlinx.coroutines.*

fun main() {
    val scopeJob = Job()
    val scope = CoroutineScope(Dispatchers.Default + scopeJob)
    //var childCoroutineJob: Job? = null
    val passedJob = Job()
    val coroutineJob = scope.launch(passedJob) {
//        childCoroutineJob = launch {
//            println("Starting a child coroutine")
//            delay(1000)
//        }
        println("Starting coroutine")
        delay(1000)
    }

    Thread.sleep(1000)
    println("passedJob and coroutineJob are references to the same job object? => ${passedJob == coroutineJob}")
//    println("Is childCoroutineJob a child of coroutineJob? => ${coroutineJob.children.contains(childCoroutineJob)}")

    println("Is coroutineJob a child of scopeJob? => ${scopeJob.children.contains(coroutineJob)}")
}