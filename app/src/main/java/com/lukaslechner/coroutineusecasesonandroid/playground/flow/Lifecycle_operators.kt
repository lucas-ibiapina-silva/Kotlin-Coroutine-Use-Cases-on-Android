package com.lukaslechner.coroutineusecasesonandroid.playground.flow

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    flow {
        emit(1)
        emit(2)
        emit(3)
    }.onStart {
        emit(0)
    }.map {
        "value $it"
    }.onStart{
        emit("hi")
    }.collect {
        println(it)
    }

}