package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.opencsv.CSVReader
import java.io.InputStreamReader
import kotlin.random.Random

data class Stock(
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCap: Float,
    val country: String,
    val currentPrice: Float,
    val currency: Currency = Currency.DOLLAR,
    val priceTrend: PriceTrend = PriceTrend.UNKNOWN
)

var initialStockData: List<Stock>? = null

fun maybeLoadStockData(context: Context) {
    if (initialStockData == null) {
        val stream = context.assets.open("stockdata.csv")
        val csvReader = CSVReader(InputStreamReader(stream))
        val stockData = csvReader
            .readAll()
            .drop(1)
            .mapNotNull { line ->
                val rank = line[0].toInt()
                val name = line[1]
                val symbol = line[2]
                val marketCap = line[3].toFloat()
                val priceUsd = line[4].toFloat()
                val country = line[5]
                Stock(rank, name, symbol, marketCap, country, priceUsd)
            }.also {
                csvReader.close()
            }
        initialStockData = stockData
    }
}

fun fakeCurrentStockPrices(context: Context): List<Stock> {
    maybeLoadStockData(context)

    return initialStockData!!.map { stock ->
        val currentPrice = stock.currentPrice
        if (stock.currentPrice == 0f) {
            return@map stock
        }
        val randomRangeInPercent = 0.03
        val randomLowerBound = (currentPrice * (1 - randomRangeInPercent))
        val randomUpperBound = (currentPrice * (1 + randomRangeInPercent))
        val randomPrice = Random.nextDouble(randomLowerBound, randomUpperBound).toFloat()
        stock.copy(currentPrice = randomPrice)
    }
}

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
    val currentPrice: Float,
    val currency: Currency = Currency.DOLLAR,
    val marketCap: Float = 0f,
    val priceTrend: PriceTrend = PriceTrend.UNKNOWN
)

enum class Currency {
    DOLLAR, EURO
}

enum class PriceTrend {
    UP, DOWN, NEUTRAL, UNKNOWN
}

fun List<CryptoCurrencyDTO>.toCryptoCurrencyList() = this.map { it.toCryptoCurrency() }

fun CryptoCurrencyDTO.toCryptoCurrency(): CryptoCurrency = CryptoCurrency(
    name = this.name,
    symbol = this.symbol,
    totalSupply = this.totalSupply,
    currentPrice = this.quote.usd.price
)

data class CurrencyRate(val usdInEuro: Float)


var currentCurrencyRate = 1.00f
fun fakeCurrentCurrencyRate(): CurrencyRate {
    val randomIncreaseOrDecrease = Random.nextDouble(-0.01, 0.01).toFloat()
    currentCurrencyRate += randomIncreaseOrDecrease
    return CurrencyRate(currentCurrencyRate)
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
            val currentPrice = it.currentPrice
            val randomRangeInPercent = 0.03
            val randomLowerBound = (currentPrice * (1 - randomRangeInPercent))
            val randomUpperBound = (currentPrice * (1 + randomRangeInPercent))
            val randomPrice = Random.nextDouble(randomLowerBound, randomUpperBound).toFloat()
            it.copy(currentPrice = randomPrice)
        }
    }

