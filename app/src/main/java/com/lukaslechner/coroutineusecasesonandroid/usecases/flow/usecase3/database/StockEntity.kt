package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock

@Entity(tableName = "stock")
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: Long,
    val priceUsd: Float
)

fun List<StockEntity>.mapToUiModelList() = map {
    Stock(currentPriceUsd = it.priceUsd)
}

fun Stock.mapToEntity() =
    StockEntity(
        time = System.currentTimeMillis(),
        priceUsd = currentPriceUsd
    )