package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface StockPriceDataSource {
    val latestPrice: Flow<Stock>
}

class NetworkStockPriceDataSource(
    private val mockApi: FlowMockApi = mockApi(),
    private val refreshIntervalMs: Long = 3000
): StockPriceDataSource {

    override val latestPrice: Flow<Stock> = flow {
        while (true) {
            val currentStockPrice = mockApi.getCurrentAlphabetStockPrice()
            emit(currentStockPrice)
            delay(refreshIntervalMs)
        }
    }
}