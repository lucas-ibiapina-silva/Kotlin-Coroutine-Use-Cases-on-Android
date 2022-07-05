package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.CryptoCurrency

@Entity(tableName = "crypto-currencies")
data class CryptoCurrencyEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val totalSupply: Int,
    val priceUsd: Float
)

fun List<CryptoCurrencyEntity>.mapToUiModelList() = map {
    CryptoCurrency(
        name = it.name,
        symbol = it.symbol,
        totalSupply = it.totalSupply,
        currentPriceUsd = it.priceUsd)
}

fun CryptoCurrency.mapToEntity() =
    CryptoCurrencyEntity(
        name = this.name,
        symbol = this.symbol,
        totalSupply = this.totalSupply,
        priceUsd = currentPriceUsd
    )

fun List<CryptoCurrency>.mapToEntityList() = map { it.mapToEntity() }