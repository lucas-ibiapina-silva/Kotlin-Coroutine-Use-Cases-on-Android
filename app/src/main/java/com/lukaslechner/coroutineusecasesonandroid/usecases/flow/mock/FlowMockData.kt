package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock

import kotlin.random.Random

data class GoogleStock(val currentPriceUsd: Float)

data class CurrencyRate(val usdInEuro: Float)

fun fakeCurrentGoogleStockPrice(): GoogleStock {
    val initialPrice = 2_000f
    val increase = Random.nextInt(100)
    val currentPrice = initialPrice + increase
    return GoogleStock(currentPrice)
}

fun fakeCurrentCurrencyRate(): CurrencyRate {
    val initial = 1.05f
    val increase = Random.nextDouble(0.0, 0.05).toFloat()
    val currentRate = initial + increase
    return CurrencyRate(currentRate)
}