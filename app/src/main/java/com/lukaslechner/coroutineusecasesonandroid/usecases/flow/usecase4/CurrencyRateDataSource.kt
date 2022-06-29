package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.CurrencyRate
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CurrencyRateDataSource(
    private val mockApi: FlowMockApi = mockApi(),
    private val refreshIntervalMs: Long = 3500
) {

    val latestRate: Flow<CurrencyRate> = flow {
        while (true) {
            val currentCurrencyRate = mockApi.getCurrentCurrencyRate()
            emit(currentCurrencyRate)
            delay(refreshIntervalMs)
        }
    }
}