package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock

import kotlin.random.Random

data class Stock(val companyName: String = "alphabet", val currentPriceUsd: Float)

data class CurrencyRate(val usdInEuro: Float)

// return random alphabet stock prices between 2000 and 2100 usd
var currentStockPriceUsd = 2_000f
fun fakeCurrentAlphabetStockPrice(): Stock {
    val randomIncreaseOrDecrease = Random.nextInt(-10, 10)
    currentStockPriceUsd += randomIncreaseOrDecrease
    return Stock(currentPriceUsd = currentStockPriceUsd)
}

// return usd-euro currency rates between 1.05 and 1.10 (usd in euro)
fun fakeCurrentCurrencyRate(): CurrencyRate {
    val initial = 1.05f
    val increase = Random.nextDouble(0.0, 0.05).toFloat()
    val currentRate = initial + increase
    return CurrencyRate(currentRate)
}