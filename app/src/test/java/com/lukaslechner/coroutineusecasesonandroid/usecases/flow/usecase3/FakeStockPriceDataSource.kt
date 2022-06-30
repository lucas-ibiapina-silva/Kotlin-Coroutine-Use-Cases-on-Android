package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeStockPriceDataSource : StockPriceDataSource {

    override val latestPrice: Flow<Stock>
        get() = flow {
            //delay(1000)
            emit(Stock(currentPriceUsd = 1.0f))
            delay(1000)
            emit(Stock(currentPriceUsd = 2.0f))
            delay(1000)
            emit(Stock(currentPriceUsd = 3.0f))
        }
}