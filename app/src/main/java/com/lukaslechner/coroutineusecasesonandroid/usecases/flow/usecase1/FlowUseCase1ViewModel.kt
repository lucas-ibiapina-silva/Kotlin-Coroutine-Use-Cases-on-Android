package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber

class FlowUseCase1ViewModel(
    private val mockApi: FlowMockApi
) : ViewModel() {

    //TODO: Problem: chart data lost on configuration change

    val latestPrice: Flow<List<Stock>> = flow {
        while (true) {
            val currentStockPrice = mockApi.getCurrentStockPrices()
            emit(currentStockPrice)
            delay(3_000)
        }
    }

    val currentStockPriceAsLiveData: LiveData<UiState> = latestPrice
        .map { stock ->
            UiState.Success(stock) as UiState
        }.onEach {
            Timber.d("Reaching onEach{}")
        }.onStart {
            emit(UiState.Loading)
        }.asLiveData()

}