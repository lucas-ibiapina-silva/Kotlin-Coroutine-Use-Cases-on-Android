package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock

@Entity(tableName = "stock")
data class StockEntity(
    val rank: Int,
    val name: String,
    @PrimaryKey val symbol: String,
    val marketCap: Float,
    val currentPriceUsd: Float,
    val country: String,
)

fun List<StockEntity>.mapToUiModelList() = map {
    Stock(
        rank = it.rank,
        name = it.name,
        symbol = it.symbol,
        marketCap = it.marketCap,
        currentPrice = it.currentPriceUsd,
        country = it.country
    )
}

fun Stock.mapToEntity() =
    StockEntity(
        rank = this.rank,
        name = this.name,
        symbol = this.symbol,
        marketCap = this.marketCap,
        currentPriceUsd = this.currentPrice,
        country = this.country
    )