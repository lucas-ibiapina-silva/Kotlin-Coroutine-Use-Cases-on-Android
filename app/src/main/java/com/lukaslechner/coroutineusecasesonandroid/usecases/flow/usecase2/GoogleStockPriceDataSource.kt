package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.GoogleStock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GoogleStockPriceDataSource(
    private val mockApi: FlowMockApi = mockApi(),
    private val refreshIntervalMs: Long = 1000
) {

    val latestPrice: Flow<GoogleStock> = flow {
        while (true) {
            val currentStockPrice = mockApi.getCurrentGoogleStockPrice()
            emit(currentStockPrice)
            delay(refreshIntervalMs)
        }
    }
}