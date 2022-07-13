package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StockPriceDataSource(
    private val mockApi: FlowMockApi = mockApi(),
    private val refreshIntervalMs: Long = 1000
) {

    val latestPrice: Flow<Stock> = flow {
        while (true) {
            // val currentStockPrice = mockApi.getCurrentAlphabetStockPrice()
            // emit(currentStockPrice)
            // delay(refreshIntervalMs)
        }
    }
}