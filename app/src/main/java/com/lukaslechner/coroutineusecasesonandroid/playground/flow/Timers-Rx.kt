package com.lukaslechner.coroutineusecasesonandroid.playground.flow

import io.reactivex.Flowable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

fun main() {
    Flowable.interval(1,1, TimeUnit.SECONDS).subscribeBy (
        onNext = {
            println("${System.currentTimeMillis()} - $it")
        }
    )

    Thread.sleep(5000)
}