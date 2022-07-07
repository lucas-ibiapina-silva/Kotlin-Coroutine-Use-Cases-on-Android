package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import kotlin.random.Random

data class Stock(val companyName: String = "alphabet", val currentPriceUsd: Float)
data class StockListing(val name: String, val symbol: String)

data class CryptoCurrencyDTO(
    val name: String,
    val symbol: String,
    @SerializedName("total_supply") val totalSupply: Float,
    val quote: Quote
)

data class Quote(@SerializedName("USD") val usd: USD)
data class USD(val price: Float)

data class CryptoCurrency(
    val name: String,
    val symbol: String,
    val totalSupply: Float,
    val currentPriceUsd: Float,
    val marketCap: Float = 0f,
    val priceTrend: PriceTrend = PriceTrend.UNKNOWN
)

enum class PriceTrend {
    UP, DOWN, NEUTRAL, UNKNOWN
}

fun List<CryptoCurrencyDTO>.toCryptoCurrencyList() = this.map { it.toCryptoCurrency() }

fun CryptoCurrencyDTO.toCryptoCurrency(): CryptoCurrency = CryptoCurrency(
    name = this.name,
    symbol = this.symbol,
    totalSupply = this.totalSupply,
    currentPriceUsd = this.quote.usd.price
)

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

val allParsedCryptoCurrencies: List<CryptoCurrency> by lazy {
    val gson = Gson()
    val cryptoCurrencyTypeToken = object : TypeToken<List<CryptoCurrencyDTO>>() {}.type
    val dto = gson.fromJson<List<CryptoCurrencyDTO>?>(allCryptos, cryptoCurrencyTypeToken)
    return@lazy dto.toCryptoCurrencyList()
}

val allCryptoCurrenciesWithRandomPrice: List<CryptoCurrency>
    get() {
        return allParsedCryptoCurrencies.map {
            val currentPrice = it.currentPriceUsd
            val randomRangeInPercent = 0.03
            val randomLowerBound = (currentPrice * (1 - randomRangeInPercent))
            val randomUpperBound = (currentPrice * (1 + randomRangeInPercent))
            val randomPrice = Random.nextDouble(randomLowerBound, randomUpperBound).toFloat()
            it.copy(currentPriceUsd = randomPrice)
        }
    }

fun filterStocks(request: Request): List<StockListing> {
    val searchTerm = request.url().queryParameter("searchTerm") ?: return emptyList()
    if (searchTerm.length < 3) {
        return emptyList()
    }
    return allParsedStocks.filter { it.name.contains(searchTerm, true) }
}

