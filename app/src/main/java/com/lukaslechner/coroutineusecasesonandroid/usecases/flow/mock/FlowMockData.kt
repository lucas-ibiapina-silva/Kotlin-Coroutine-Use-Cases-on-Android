package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import kotlin.random.Random

data class Stock(val companyName: String = "alphabet", val currentPriceUsd: Float)
data class StockListing(val name: String, val symbol: String)

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

val allParsedStocks: List<StockListing> by lazy {
    val gson = Gson()
    val stockType = object : TypeToken<List<StockListing>>() {}.type
    return@lazy gson.fromJson<List<StockListing>?>(allStocks, stockType)
}

fun filterStocks(request: Request): List<StockListing> {
    val searchTerm = request.url().queryParameter("searchTerm") ?: return emptyList()
    if (searchTerm.length < 3) {
        return emptyList()
    }
    return allParsedStocks.filter { it.name.contains(searchTerm, true) }
}

