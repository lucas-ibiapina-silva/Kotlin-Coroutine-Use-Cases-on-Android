package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock

import kotlin.random.Random

data class Stock(val companyName: String = "alphabet", val currentPriceUsd: Float)

data class CurrencyRate(val usdInEuro: Float)

var currentStockPriceUsd = 2_000f
fun fakeCurrentAlphabetStockPrice(): Stock {
    val randomIncreaseOrDecrease = Random.nextInt(-5, 5)
    currentStockPriceUsd += randomIncreaseOrDecrease
    return Stock(currentPriceUsd = currentStockPriceUsd)
}

var currentCurrencyRate = 1.00f
fun fakeCurrentCurrencyRate(): CurrencyRate {
    val randomIncreaseOrDecrease = Random.nextDouble(-0.01, 0.01).toFloat()
    currentCurrencyRate += randomIncreaseOrDecrease
    return CurrencyRate(randomIncreaseOrDecrease)
}